package com.chewite.app.ui.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chewite.app.data.api.chewite.AccountApi
import com.chewite.app.data.local.AuthTokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val authTokenStorage: AuthTokenStorage, private val api: AccountApi
) : ViewModel() {

    companion object {
        private const val TAG = "RecipeListViewModel"
    }

    fun test() {
        viewModelScope.launch {
            val accessToken = authTokenStorage.load()?.accessToken
            accessToken?.let {
                Log.i(TAG, "${api.getMyInfo(it)}")
            }
        }
    }
}