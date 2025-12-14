package com.chewite.app.data.auth

import android.util.Base64
import android.util.Log
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.amazonaws.services.cognitoidentityprovider.model.AuthFlowType
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthRequest
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeResult
import com.chewite.app.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authClient: AmazonCognitoIdentityProviderClient,
    private val authDataStore: AuthDataStore
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepositoryImpl"
        private const val COGNITO_CLIENT_ID = "2q49e9a4qvgm95a3ail5t0gp0b"
    }

    override suspend fun getAccessToken(socialIdToken: String): String? {
        return runCatching {
            val saved = authDataStore.load()
            when {
                saved == null -> loginWithSocial(socialIdToken)
                saved.isAccessTokenExpired() -> refresh(saved)
                else -> {
                    Log.i(TAG, "Use saved access token")
                    saved.accessToken
                }
            }
        }.onFailure { t ->
            Log.e(TAG, "getAccessToken failed", t)
        }.getOrNull()
    }

    private suspend fun loginWithSocial(socialIdToken: String): String {
        Log.i(TAG, "loginWithSocial")
        val userEmail = getUserEmail(socialIdToken)
        val init = initiate(userEmail)
        val session = init.session ?: error("No session returned. challenge=${init.challengeName}")
        val resp = respond(username = userEmail, session = session, socialIdToken = socialIdToken)
        val auth = resp.authenticationResult
            ?: error("Not authenticated yet. challenge=${resp.challengeName}, session=${resp.session}")

        return AuthData(
            accessToken = auth.accessToken,
            refreshToken = auth.refreshToken,
            idToken = auth.idToken,
            expiresIn = auth.expiresIn,
            issuedAtEpochSec = System.currentTimeMillis() / 1000
        ).also { authDataStore.save(it) }.accessToken
    }

    private suspend fun initiate(username: String): InitiateAuthResult {
        return withContext(Dispatchers.IO) {
            authClient.initiateAuth(InitiateAuthRequest().apply {
                authFlow = AuthFlowType.CUSTOM_AUTH.toString()
                clientId = COGNITO_CLIENT_ID
                authParameters = mapOf("USERNAME" to username)
            })
        }
    }

    private suspend fun respond(
        username: String, session: String, socialIdToken: String
    ): RespondToAuthChallengeResult {
        return withContext(Dispatchers.IO) {
            authClient.respondToAuthChallenge(
                RespondToAuthChallengeRequest().apply {
                    clientId = COGNITO_CLIENT_ID
                    this.session = session
                    this.challengeName = "CUSTOM_CHALLENGE"
                    challengeResponses = mapOf("USERNAME" to username, "ANSWER" to socialIdToken)
                })
        }
    }

    private suspend fun refresh(saved: AuthData): String {
        Log.i(TAG, "refresh")
        return withContext(Dispatchers.IO) {
            val result = authClient.initiateAuth(
                InitiateAuthRequest().apply {
                    authFlow = AuthFlowType.REFRESH_TOKEN_AUTH.toString()
                    clientId = COGNITO_CLIENT_ID
                    authParameters = mapOf("REFRESH_TOKEN" to saved.refreshToken)
                })
            val auth = result.authenticationResult ?: error("No authenticationResult from refresh")
            val updated = saved.copy(
                accessToken = auth.accessToken,
                expiresIn = auth.expiresIn,
                issuedAtEpochSec = System.currentTimeMillis() / 1000
            )
            authDataStore.save(updated)

            auth.accessToken
        }
    }

    private fun getUserEmail(idToken: String): String {
        val parts = idToken.split(".")
        val payload = String(
            Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8
        )
        return JSONObject(payload).optString("email")
    }
}