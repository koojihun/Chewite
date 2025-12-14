package com.chewite.app.presentation.login.auth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.chewite.app.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthProvider(
    private val activity: ComponentActivity,
    private val credentialManager: CredentialManager,
) : SocialAuthProvider {
    companion object {
        private const val TAG = "GoogleAuthProvider"
    }

    private val googleSignInRequest by lazy {
        val googleOption = GetSignInWithGoogleOption.Builder(
            activity.getString(R.string.google_server_client_id)
        ).build()
        GetCredentialRequest(listOf(googleOption))
    }

    private fun extractGoogleIdToken(credential: Credential): String? {
        val custom = credential as? CustomCredential ?: return null
        if (custom.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return null
        return GoogleIdTokenCredential.createFrom(custom.data).idToken
    }

    override suspend fun getIdToken(): String? = runCatching {
        val response = credentialManager.getCredential(activity, googleSignInRequest)
        extractGoogleIdToken(response.credential)
    }.onFailure { t ->
        Log.e(TAG, "Google login failed", t)
    }.getOrNull()
}