package com.chewite.app.ui.signup

import android.os.Bundle
import com.chewite.app.databinding.ActivitySignupBinding
import com.chewite.app.ui.BaseActivity

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}