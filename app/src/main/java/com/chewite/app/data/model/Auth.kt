package com.chewite.app.data.model

data class Auth(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String,
    val expiresIn: Int,
    val issuedAtEpochSec: Long
) {
    fun isAccessTokenExpired(): Boolean {
        val nowEpochSec = System.currentTimeMillis() / 1000
        val safetyMargin = 60
        return nowEpochSec >= (issuedAtEpochSec + expiresIn - safetyMargin)
    }
}