package com.chewite.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chewite.app.data.account.UserInfo
import com.chewite.app.domain.repository.AccountRepository
import com.chewite.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository, private val accountRepository: AccountRepository
) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun verifyGoogleToken(idToken: String) {
        _loginState.value = LoginUiState.Loading
        viewModelScope.launch {
            val accessToken = authRepository.getAccessToken(idToken)
            accessToken?.let { accountRepository.login(it) }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val data: UserInfo) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

