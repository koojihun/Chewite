package com.chewite.app.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chewite.app.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    val currentUser = userRepository.currentUser

    private val _text = MutableLiveData<String>().apply {
        value = "마이페이지 화면 입니다"
    }
    val text: LiveData<String> = _text
}