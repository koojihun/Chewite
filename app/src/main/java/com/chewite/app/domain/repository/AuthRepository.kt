package com.chewite.app.domain.repository

interface AuthRepository {
    suspend fun verifyGoogleLogin(socialIdToken: String): String?
}