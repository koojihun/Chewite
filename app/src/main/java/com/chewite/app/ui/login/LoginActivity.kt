package com.chewite.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.chewite.app.R
import com.chewite.app.databinding.ActivityLoginBinding
import com.chewite.app.ui.BaseActivity
import com.chewite.app.ui.signup.SignUpActivity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setXButtonClickListener()
        setLoginButtonsClickListener()
    }

    private fun setXButtonClickListener() = binding.xBtn.setOnClickListener { finish() }

    private fun setLoginButtonsClickListener() {
        binding.naverLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.kakaoLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.googleLoginBtn.setOnClickListener {
            val credentialManager = CredentialManager.create(this)

            val googleOption = GetSignInWithGoogleOption.Builder(
                getString(R.string.google_server_client_id)
            ).build()

            val request = GetCredentialRequest(listOf(googleOption))
            lifecycleScope.launch {
                try {
                    // 1) 구글 계정 선택 (UI 스레드 OK)
                    val credentialResponse = credentialManager.getCredential(
                        this@LoginActivity, request
                    )

                    // 2) Google ID Token 추출
                    val cred = credentialResponse.credential
                    when (cred) {
                        is CustomCredential -> {
                            if (cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                GoogleIdTokenCredential.createFrom(cred.data).idToken
                            } else {
                                throw IllegalStateException("Unsupported credential type: ${cred.type}")
                            }
                        }

                        else -> throw IllegalStateException("Unexpected credential class: ${cred::class.java}")
                    }

                    startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))

                    // 3) 백엔드 검증(네트워크는 IO로)
//                    val myToken = withContext(Dispatchers.IO) {
//                        api.verifyGoogleIdToken(VerifyReq(idToken))
//                    }

                    // 4) 앱 세션 저장
//                    sessionStore.save(myToken)

                } catch (e: GetCredentialException) {
                    Log.i("TEST_LOG_TAG", "Credential error: $e")
                    // 사용자 취소/계정없음/네트워크 등 UX 처리
                } catch (e: ClassCastException) {
                    Log.e("TEST_LOG_TAG", "Cast error: $e")
                }

            }
        }
    }
}