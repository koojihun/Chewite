package com.chewite.app.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.credentials.CredentialManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chewite.app.databinding.ActivityLoginBinding
import com.chewite.app.presentation.BaseActivity
import com.chewite.app.presentation.login.auth.GoogleAuthProvider
import com.chewite.app.presentation.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val credentialManager by lazy { CredentialManager.create(this) }
    private val googleAuthProvider by lazy { GoogleAuthProvider(this, credentialManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setXButtonClickListener()
        setLoginButtonsClickListener()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginUiState.Loading -> {
                            Log.d(TAG, "Loading...")
                        }

                        is LoginUiState.Success -> {
                            Log.i(TAG, "Success: ${state.data}")
                        }

                        is LoginUiState.Error -> {
                            Log.e(TAG, "Error: ${state.message}")
                        }

                        LoginUiState.Idle -> {}
                    }
                }
            }
        }
    }

    private fun setXButtonClickListener() = binding.xBtn.setOnClickListener { finish() }

    private fun setLoginButtonsClickListener() {
        binding.naverLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.kakaoLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.googleLoginBtn.setOnClickListener { handleGoogleLogin() }
    }

    private fun handleGoogleLogin() {
        lifecycleScope.launch {
            val idToken = googleAuthProvider.getIdToken()
            if (idToken == null) {
                Log.w(TAG, "Google credential returned but idToken was null")
            } else {
                Log.i(TAG, "idToken = $idToken")
                viewModel.verifyGoogleToken(idToken)
            }
        }
    }
}
