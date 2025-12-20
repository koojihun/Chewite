package com.chewite.app.data.api.auth.social

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.chewite.app.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential


class GoogleAuthProvider(
    private val applicationContext: Context,
    private val credentialManager: CredentialManager,
) : SocialAuthProvider {
    companion object {
        private const val TAG = "GoogleAuthProvider"
    }

    private val googleSignInRequest by lazy {
        val googleOption = GetSignInWithGoogleOption.Builder(
            applicationContext.getString(R.string.google_server_client_id)
        ).build()
        GetCredentialRequest(listOf(googleOption))
    }

    private fun extractGoogleIdToken(credential: Credential): String? {
        val custom = credential as? CustomCredential ?: return null
        if (custom.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return null
        return GoogleIdTokenCredential.createFrom(custom.data).idToken
    }

    override suspend fun getIdToken(context: Context): String? = runCatching {
        val response = credentialManager.getCredential(context, googleSignInRequest)
        extractGoogleIdToken(response.credential)
    }.onFailure { t ->
        Log.e(TAG, "Google login failed", t)
    }.getOrNull()
}