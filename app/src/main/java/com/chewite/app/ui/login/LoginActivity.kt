package com.chewite.app.ui.login

import android.content.Intent
import android.os.Bundle
import com.chewite.app.databinding.ActivityLoginBinding
import com.chewite.app.ui.BaseActivity
import com.chewite.app.ui.signup.SignUpActivity

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setXButtonClickListener()
        setLoginButtonsClickListener()
    }

    private fun setXButtonClickListener() {
        binding.xBtn.setOnClickListener { finish() }
    }

    private fun setLoginButtonsClickListener() {
        binding.naverLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        binding.kakaoLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        binding.googleLoginBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
}