package com.chewite.app.data.account

import android.util.Log
import com.chewite.app.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountApi: AccountApi
) : AccountRepository {
    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }

    override suspend fun login(accessToken: String) = withContext(Dispatchers.IO) {
        Log.i(TAG, "login with access token")
        val myInfo = accountApi.getMyInfo(accessToken)
        when (myInfo.status) {
            "NEW" -> {
                Log.i(TAG, "New Member -> Sign Up Process starts.")
                val signUpResult = accountApi.signUp(
                    accessToken, SignUpInfo(true, true, true)
                )
                Log.i(TAG, "${signUpResult}")
            }

            "ACTIVE" -> {
                Log.i(TAG, "Active Member.")
            }
        }
    }
}