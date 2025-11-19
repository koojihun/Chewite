package com.chewite.app.ui.terms

import android.os.Bundle
import com.chewite.app.data.terms.TermsType
import com.chewite.app.databinding.ActivityTermsBinding
import com.chewite.app.ui.BaseActivity

class TermsActivity : BaseActivity() {

    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getStringExtra(TermsType.EXTRA_KEY)
            ?.let { runCatching { TermsType.valueOf(it) }.getOrNull() } ?: run { finish(); return }

        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBackButton()
        setText(type)
    }

    private fun setBackButton() {
        binding.xBtn.setOnClickListener { finish() }
    }

    private fun setText(type: TermsType) {
        binding.titleTextView.setText(type.titleId)
        val content = resources.openRawResource(type.detailsId).bufferedReader().readText()
        binding.detailTextView.text = content
    }
}