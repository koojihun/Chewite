package com.chewite.app.presentation.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "마이페이지 화면 입니다"
    }
    val text: LiveData<String> = _text
}