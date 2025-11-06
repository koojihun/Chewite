package com.chewite.app.ui.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.chewite.app.data.signup.CONSENT_MARKETING_KEY
import com.chewite.app.data.signup.CONSENT_PERSONAL_INFO_KEY
import com.chewite.app.data.signup.CONSENT_SERVICE_KEY
import com.chewite.app.data.signup.ConsentItem
import com.chewite.app.data.signup.ConsentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SignUpViewModel : ViewModel() {
    private val _consent = MutableStateFlow(
        ConsentState(
            items = listOf(
                ConsentItem(CONSENT_SERVICE_KEY, required = true, agreed = false),
                ConsentItem(CONSENT_PERSONAL_INFO_KEY, required = true, agreed = false),
                ConsentItem(CONSENT_MARKETING_KEY, required = false, agreed = false),
            )
        )
    )
    val consent: StateFlow<ConsentState> = _consent

    private val _profileImage = MutableStateFlow<Uri>(Uri.EMPTY)
    val profileImage: StateFlow<Uri> = _profileImage

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    fun agreeAll() {
        listOf(CONSENT_SERVICE_KEY, CONSENT_PERSONAL_INFO_KEY, CONSENT_MARKETING_KEY).forEach {
            setAgreed(it, true)
        }
    }

    fun declineAll() {
        listOf(CONSENT_SERVICE_KEY, CONSENT_PERSONAL_INFO_KEY, CONSENT_MARKETING_KEY).forEach {
            setAgreed(it, false)
        }
    }

    fun setAgreed(key: String, agreed: Boolean) {
        val updated = _consent.value.items.map { item ->
            if (item.key == key)
                item.copy(agreed = agreed)
            else item
        }
        _consent.value = _consent.value.copy(items = updated)
    }

    private fun getNowString(): String {
        val now = System.currentTimeMillis()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) a hh:mm")
        return Instant.ofEpochMilli(now)
            .atZone(ZoneId.of("Asia/Seoul"))
            .format(formatter)
    }

    fun updateNickname(text: String) {
        _nickname.value = text
    }
}
