package com.chewite.app.data

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
    private val idpClient: AmazonCognitoIdentityProviderClient
) : AuthRepository {

    private val COGNITO_CLIENT_ID = "2q49e9a4qvgm95a3ail5t0gp0b"

    override suspend fun verifyGoogleLogin(socialIdToken: String): String? {
        try {
            return loginWithSocial(socialIdToken)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.toString())
            return null
        }
    }

    suspend fun loginWithSocial(socialIdToken: String): String {
        val userEmail = getUserEmail(socialIdToken)
        val init = initiate(userEmail)
        val session = init.session ?: error("No session returned. challenge=${init.challengeName}")
        val resp = respond(
            username = userEmail, session = session, socialIdToken = socialIdToken
        )
        val auth = resp.authenticationResult
            ?: error("Not authenticated yet. challenge=${resp.challengeName}, session=${resp.session}")

        Log.i("TEST_LOG_TAG", "access token: ${auth.accessToken}")
        return auth.accessToken
    }

    suspend fun initiate(username: String): InitiateAuthResult = withContext(Dispatchers.IO) {
        idpClient.initiateAuth(
            InitiateAuthRequest().apply {
                authFlow = AuthFlowType.CUSTOM_AUTH.toString()
                clientId = COGNITO_CLIENT_ID
                authParameters = mapOf(
                    "USERNAME" to username
                )
            })
    }

    suspend fun respond(
        username: String,
        session: String,
        socialIdToken: String,
        challengeName: String = "CUSTOM_CHALLENGE"
    ): RespondToAuthChallengeResult = withContext(Dispatchers.IO) {
        idpClient.respondToAuthChallenge(
            RespondToAuthChallengeRequest().apply {
                clientId = COGNITO_CLIENT_ID
                this.session = session
                this.challengeName = challengeName
                challengeResponses = mapOf(
                    "USERNAME" to username, "ANSWER" to socialIdToken
                )
            })
    }

    fun getUserEmail(idToken: String): String {
        val parts = idToken.split(".")
        val payload = String(
            Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8
        )
        return JSONObject(payload).optString("email")
    }
}