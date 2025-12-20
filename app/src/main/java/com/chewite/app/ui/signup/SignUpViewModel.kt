package com.chewite.app.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chewite.app.data.api.chewite.AccountApi
import com.chewite.app.data.local.AuthTokenStorage
import com.chewite.app.data.model.SignUpInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authTokenStorage: AuthTokenStorage, private val accountApi: AccountApi
) : ViewModel() {

    private val _agreements = MutableStateFlow(
        listOf(
            AgreementItem(1, "서비스 이용약관", true, type = AgreementType.SERVICE),
            AgreementItem(2, "개인정보 처리방침", true, type = AgreementType.PERSONAL),
            AgreementItem(3, "마케팅 활용 동의", false, type = AgreementType.MARKETING)
        )
    )
    val agreements = _agreements.asStateFlow()

    fun toggleAgreement(item: AgreementItem) {
        _agreements.update { list ->
            list.map { if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it }
        }
    }

    fun toggleAll(isChecked: Boolean) {
        _agreements.update { list ->
            list.map { it.copy(isChecked = isChecked) }
        }
    }

    fun isAllRequiredAgreed(): Boolean =
        _agreements.value.filter { it.isRequired }.all { it.isChecked }

    fun signUpFinish(onSignUpFinish: () -> Unit) {
        viewModelScope.launch {
            val auth = authTokenStorage.load()
            auth?.let {
                accountApi.signUp(
                    it.accessToken, SignUpInfo(
                        _agreements.value[0].isChecked,
                        _agreements.value[1].isChecked,
                        _agreements.value[2].isChecked
                    )
                )
                onSignUpFinish()
            }
        }
    }
}

data class AgreementItem(
    val id: Int,
    val title: String,
    val isRequired: Boolean,
    val isChecked: Boolean = false,
    val type: AgreementType
)

enum class AgreementType(val title: String, val fileName: String) {
    SERVICE("서비스 이용약관", "service_agreement.txt"), PERSONAL(
        "개인정보 처리방침", "personal_agreement.txt"
    ),
    MARKETING("마케팅 활용 동의", "marketing_agreement.txt")
}