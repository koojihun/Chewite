package com.chewite.app.data.api.auth.aws

import android.util.Base64
import android.util.Log
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.amazonaws.services.cognitoidentityprovider.model.AuthFlowType
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthRequest
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeResult
import com.chewite.app.data.local.AuthTokenStorage
import com.chewite.app.data.model.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AwsProvider @Inject constructor(
    private val authClient: AmazonCognitoIdentityProviderClient,
    private val authTokenStorage: AuthTokenStorage
) {
    companion object {
        private const val TAG = "AwsProvider"
        private const val COGNITO_CLIENT_ID = "2q49e9a4qvgm95a3ail5t0gp0b"
    }

    suspend fun getAccessToken(idToken: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val saved = authTokenStorage.load()
            when {
                saved == null -> loginWithSocial(idToken)
                saved.isAccessTokenExpired() -> refresh(saved)
                else -> {
                    Log.i(TAG, "Use saved access token")
                    saved.accessToken
                }
            }
        }.onFailure { t ->
            Log.e(TAG, "getAccessToken failed", t)
            Log.i(TAG, t.toString())
        }.getOrNull()
    }

    private fun loginWithSocial(socialIdToken: String): String {
        Log.i(TAG, "loginWithSocial")
        val userEmail = getUserEmail(socialIdToken)
        val init = initiate(userEmail)
        val session = init.session ?: error("No session returned. challenge=${init.challengeName}")
        val resp = respond(username = userEmail, session = session, socialIdToken = socialIdToken)
        val auth = resp.authenticationResult
            ?: error("Not authenticated yet. challenge=${resp.challengeName}, session=${resp.session}")

        return Auth(
            accessToken = auth.accessToken,
            refreshToken = auth.refreshToken,
            idToken = auth.idToken,
            expiresIn = auth.expiresIn,
            issuedAtEpochSec = System.currentTimeMillis() / 1000
        ).also { authTokenStorage.save(it) }.accessToken
    }

    private fun initiate(userEmail: String): InitiateAuthResult {
        return authClient.initiateAuth(
            InitiateAuthRequest().apply {
                authFlow = AuthFlowType.CUSTOM_AUTH.toString()
                clientId = COGNITO_CLIENT_ID
                authParameters = mapOf("USERNAME" to userEmail)
            })
    }

    private fun respond(
        username: String, session: String, socialIdToken: String
    ): RespondToAuthChallengeResult {
        return authClient.respondToAuthChallenge(
            RespondToAuthChallengeRequest().apply {
                clientId = COGNITO_CLIENT_ID
                this.session = session
                this.challengeName = "CUSTOM_CHALLENGE"
                challengeResponses = mapOf("USERNAME" to username, "ANSWER" to socialIdToken)
            })
    }

    fun refresh(saved: Auth): String {
        Log.i(TAG, "refresh")
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
        authTokenStorage.save(updated)
        return auth.accessToken
    }

    private fun getUserEmail(idToken: String): String {
        val parts = idToken.split(".")
        val payload = String(
            Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8
        )
        return JSONObject(payload).optString("email")
    }
}