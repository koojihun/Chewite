package com.chewite.app.ui.signup

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow

interface NextButtonHost {
    fun setNextOnClick(handler: () -> Unit)
    fun bindNextEnabled(owner: LifecycleOwner, enabledFlow: Flow<Boolean>)
}