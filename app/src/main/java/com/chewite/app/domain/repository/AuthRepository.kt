package com.chewite.app.domain.repository

interface AuthRepository {
    suspend fun getAccessToken(socialIdToken: String): String?
}