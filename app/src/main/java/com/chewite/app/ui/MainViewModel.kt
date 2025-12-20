package com.chewite.app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chewite.app.data.api.auth.aws.AwsProvider
import com.chewite.app.data.api.chewite.AccountApi
import com.chewite.app.data.local.AuthTokenStorage
import com.chewite.app.data.model.LoginStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authTokenStorage: AuthTokenStorage,
    private val awsProvider: AwsProvider,
    private val accountApi: AccountApi
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _loginStatus = MutableStateFlow(LoginStatus.NOT_INIT)
    val loginStatus = _loginStatus.asStateFlow()

    init {
        viewModelScope.launch { checkLoginStatus() }
    }

    suspend fun checkLoginStatus() = runCatching {
        val auth = authTokenStorage.load()
        if (auth == null) {
            Log.i(TAG, "No Auth")
            _loginStatus.value = LoginStatus.NO_AUTH
        } else {
            var accessToken = auth.accessToken
            if (auth.isAccessTokenExpired()) {
                Log.i(TAG, "Refresh Access Token")
                accessToken = awsProvider.refresh(auth)
            }
            val userInfo = accountApi.getMyInfo(accessToken)
            Log.i(TAG, "UserInfo Status: ${userInfo.status}")
            when (userInfo.status) {
                "ACTIVE" -> _loginStatus.value = LoginStatus.ACTIVE
                else -> _loginStatus.value = LoginStatus.NEW
            }
        }
    }.onFailure {
        Log.i(TAG, "Exception in checkLoginStatus")
        _loginStatus.value = LoginStatus.NO_AUTH
    }
}