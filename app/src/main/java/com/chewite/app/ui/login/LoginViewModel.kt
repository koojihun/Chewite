package com.chewite.app.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chewite.app.data.model.LoginResult
import com.chewite.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _sideEffect = Channel<LoginSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun clearAuthData() {
        authRepository.clearAuthData()
    }

    fun googleLogin(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val loginResult = authRepository.googleLogin(context)
            when (loginResult) {
                LoginResult.NEW -> _sideEffect.send(LoginSideEffect.NavigateToSignUp)
                LoginResult.ACTIVE -> _sideEffect.send(LoginSideEffect.NavigateToHome)
                else -> {}
            }
            _isLoading.value = false
        }
    }
}

sealed class LoginSideEffect {
    data object NavigateToHome : LoginSideEffect()
    data object NavigateToSignUp : LoginSideEffect()
}