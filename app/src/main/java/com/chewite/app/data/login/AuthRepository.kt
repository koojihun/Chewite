package com.chewite.app.data.login

import android.util.Base64
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.amazonaws.services.cognitoidentityprovider.model.AuthFlowType
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthRequest
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

interface AuthRepository {
    suspend fun verifyGoogleLogin(idToken: String)
}

class AuthRepositoryImpl @Inject constructor(
    private val userPool: CognitoUserPool,
    private val authApi: AuthApi,
    private val idpClient: AmazonCognitoIdentityProviderClient
) : AuthRepository {

    override suspend fun verifyGoogleLogin(idToken: String) {
        try {
            loginWithSocial(idToken)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", e.toString())
        }
    }

    suspend fun loginWithSocial(socialIdToken: String): String {
        val userEmail = getUserEmail(socialIdToken)

        val init = initiate(userEmail)

        val session = init.session ?: error("No session returned. challenge=${init.challengeName}")

        // 2) 챌린지 응답 (id_token 제출)
        val resp = respond(
            username = userEmail,
            session = session,
            socialIdToken = socialIdToken
        )

        // 3) 성공하면 Cognito 토큰이 내려옴
        val auth = resp.authenticationResult
            ?: error("Not authenticated yet. challenge=${resp.challengeName}, session=${resp.session}")

        Log.i("AuthRepositoryImpl", "access token: ${auth.accessToken}")
        return auth.accessToken
    }


    suspend fun initiate(username: String): InitiateAuthResult =
        withContext(Dispatchers.IO) {
            idpClient.initiateAuth(
                InitiateAuthRequest().apply {
                    authFlow = AuthFlowType.CUSTOM_AUTH.toString()
                    clientId = userPool.clientId
                    authParameters = mapOf(
                        "USERNAME" to username
                    )
                }
            )
        }

    suspend fun respond(
        username: String,
        session: String,
        socialIdToken: String,
        challengeName: String = "CUSTOM_CHALLENGE"
    ): RespondToAuthChallengeResult =
        withContext(Dispatchers.IO) {
            idpClient.respondToAuthChallenge(
                RespondToAuthChallengeRequest().apply {
                    clientId = userPool.clientId
                    this.session = session
                    this.challengeName = challengeName
                    challengeResponses = mapOf(
                        "USERNAME" to username,
                        "ANSWER" to socialIdToken
                    )
                }
            )
        }

    fun getUserEmail(idToken: String): String {
        val parts = idToken.split(".")
        val payload = String(
            Base64.decode(parts[1], Base64.URL_SAFE),
            Charsets.UTF_8
        )
        return JSONObject(payload).optString("email")
    }
}

