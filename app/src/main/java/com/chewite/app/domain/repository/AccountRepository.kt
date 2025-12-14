package com.chewite.app.domain.repository

interface AccountRepository {
    suspend fun login(accessToken: String)
}