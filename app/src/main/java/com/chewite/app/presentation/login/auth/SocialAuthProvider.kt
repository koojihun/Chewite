package com.chewite.app.presentation.login.auth

interface SocialAuthProvider {
    suspend fun getIdToken(): String?
}