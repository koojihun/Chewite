package com.chewite.app.data.repository

import android.content.Context
import android.util.Log
import com.chewite.app.data.api.auth.aws.AwsProvider
import com.chewite.app.data.api.auth.social.SocialAuthProvider
import com.chewite.app.data.api.chewite.AccountApi
import com.chewite.app.data.local.AuthTokenStorage
import com.chewite.app.data.model.LoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @param:Named("GoogleAuthProvider") private val googleAuthProvider: SocialAuthProvider,
    private val awsProvider: AwsProvider,
    private val accountApi: AccountApi,
    private val authTokenStorage: AuthTokenStorage
) {

    companion object {
        private const val TAG = "AuthRepository"
    }

    fun clearAuthData() {
        authTokenStorage.clear()
    }

    suspend fun googleLogin(context: Context): LoginResult = withContext(Dispatchers.IO) {
        val idToken = googleAuthProvider.getIdToken(context)
        if (idToken == null) return@withContext LoginResult.SOCIAL_FAILED

        val accessToken = awsProvider.getAccessToken(idToken)
        if (accessToken == null) return@withContext LoginResult.AWS_FAILED

        when (getMemberStatus(accessToken)) {
            "ACTIVE" -> return@withContext LoginResult.ACTIVE
            else -> return@withContext LoginResult.NEW
        }
    }

    private suspend fun getMemberStatus(accessToken: String): String {
        Log.i(TAG, "login with access token")
        val myInfo = accountApi.getMyInfo(accessToken)
        return myInfo.status
    }
}