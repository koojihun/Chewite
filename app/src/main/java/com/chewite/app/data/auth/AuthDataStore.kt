package com.chewite.app.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

class AuthDataStore @Inject constructor(
    @Named("auth") private val dataStore: DataStore<Preferences>
) {
    suspend fun save(tokens: AuthData) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS] = tokens.accessToken
            prefs[KEY_REFRESH] = tokens.refreshToken
            prefs[KEY_ID] = tokens.idToken
            prefs[KEY_EXPIRES_IN] = tokens.expiresIn
            prefs[KEY_ISSUED_AT] = tokens.issuedAtEpochSec
        }
    }

    suspend fun load(): AuthData? {
        val prefs = dataStore.data.first()
        val access = prefs[KEY_ACCESS] ?: return null
        val refresh = prefs[KEY_REFRESH] ?: return null
        val id = prefs[KEY_ID] ?: return null
        val expiresIn = prefs[KEY_EXPIRES_IN] ?: return null
        val issuedAt = prefs[KEY_ISSUED_AT] ?: return null
        return AuthData(
            accessToken = access,
            refreshToken = refresh,
            idToken = id,
            expiresIn = expiresIn,
            issuedAtEpochSec = issuedAt
        )
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_ACCESS = stringPreferencesKey("access_token")
        private val KEY_REFRESH = stringPreferencesKey("refresh_token")
        private val KEY_ID = stringPreferencesKey("id_token")
        private val KEY_EXPIRES_IN = intPreferencesKey("expires_in")
        private val KEY_ISSUED_AT = longPreferencesKey("issued_at")
    }
}