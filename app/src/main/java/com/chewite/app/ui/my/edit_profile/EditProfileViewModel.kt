package com.chewite.app.ui.my.edit_profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditProfileViewModel : ViewModel() {

    private val _profileImage = MutableStateFlow<Uri>(Uri.EMPTY)
    val profileImage: StateFlow<Uri> = _profileImage

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    fun updateNickname(text: String) {
        _nickname.value = text
    }

    fun updateProfileImage(uri: Uri) {
        _profileImage.value = uri
    }

    fun clearProfileImage() {
        _profileImage.value = Uri.EMPTY
    }
}