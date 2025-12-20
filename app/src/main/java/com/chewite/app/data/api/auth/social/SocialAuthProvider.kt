package com.chewite.app.data.api.auth.social

import android.content.Context

interface SocialAuthProvider {
    suspend fun getIdToken(context: Context): String?
}