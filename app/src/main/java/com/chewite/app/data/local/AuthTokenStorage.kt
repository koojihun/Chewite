package com.chewite.app.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.chewite.app.data.model.Auth
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Singleton
class AuthTokenStorage @Inject constructor(
    @param:Named("AuthSharedPrefs") private val prefs: SharedPreferences
) {
    fun save(tokens: Auth) {
        prefs.edit {
            putString(KEY_ACCESS, tokens.accessToken)
            putString(KEY_REFRESH, tokens.refreshToken)
            putString(KEY_ID, tokens.idToken)
            putInt(KEY_EXPIRES_IN, tokens.expiresIn)
            putLong(KEY_ISSUED_AT, tokens.issuedAtEpochSec)
        }
    }

    fun load(): Auth? {
        val accessToken = prefs.getString(KEY_ACCESS, null)
        val refreshToken = prefs.getString(KEY_REFRESH, null)
        val idToken = prefs.getString(KEY_ID, null)

        val expiresIn = prefs.getInt(KEY_EXPIRES_IN, -1)
        val issuedAt = prefs.getLong(KEY_ISSUED_AT, -1L)

        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank() || idToken.isNullOrBlank() || expiresIn == -1 || issuedAt == -1L) {
            return null
        }

        return Auth(
            accessToken = accessToken,
            refreshToken = refreshToken,
            idToken = idToken,
            expiresIn = expiresIn,
            issuedAtEpochSec = issuedAt
        )
    }

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_ID = "id_token"
        private const val KEY_EXPIRES_IN = "expires_in"
        private const val KEY_ISSUED_AT = "issued_at"
    }
}