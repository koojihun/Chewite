package com.chewite.app.presentation.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookmarkViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "북마크 화면 입니다"
    }
    val text: LiveData<String> = _text
}