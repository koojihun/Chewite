package com.chewite.app.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.chewite.app.R
import com.chewite.app.databinding.ActivitySignupBinding
import com.chewite.app.ui.BaseActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SignUpActivity : BaseActivity(), NextButtonHost {

    private lateinit var binding: ActivitySignupBinding
    private val navHost by lazy { supportFragmentManager.findFragmentById(R.id.signup_nav_host) as NavHostFragment }
    private val navController by lazy { navHost.navController }
    private var nextEnabledJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBackButton()
        setNavController()
    }

    private fun setNavController() {
        navController.addOnDestinationChangedListener { _, dest, _ ->
            updateToolbar(dest.id)
            updateBackButton(dest.id)
            updateNextButton(dest.id)
        }
    }

    private fun setBackButton() {
        binding.leftBtn.setOnClickListener { navController.navigateUp() }
    }

    private fun updateToolbar(destId: Int) {
        if (destId == R.id.navigation_signup_finish)
            binding.toolbar.visibility = View.GONE
        else
            binding.toolbar.visibility = View.VISIBLE
    }

    private fun updateBackButton(destId: Int) {
        if (destId == R.id.navigation_signup_agree)
            binding.leftBtn.visibility = View.INVISIBLE
        else
            binding.leftBtn.visibility = View.VISIBLE
    }

    private fun updateNextButton(destId: Int) {
        binding.nextButton.text = when (destId) {
            R.id.navigation_signup_agree -> "다음"
            R.id.navigation_signup_profile -> "회원가입"
            else -> "멍펫 시작하기"
        }
    }

    override fun setNextOnClick(handler: () -> Unit) {
        binding.nextButton.setOnClickListener { handler() }
    }

    override fun bindNextEnabled(
        owner: LifecycleOwner,
        enabledFlow: Flow<Boolean>
    ) {
        nextEnabledJob?.cancel()
        nextEnabledJob = owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                enabledFlow.collect { enabled ->
                    binding.nextButton.isEnabled = enabled
                }
            }
        }
    }

    override fun setTopPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2) 버튼만 IME 높이만큼 역이동(애니메이션 프레임마다)
        ViewCompat.setWindowInsetsAnimationCallback(
            binding.nextButton,
            object : WindowInsetsAnimationCompat.Callback(
                WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
            ) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    running: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val sysBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    val offset = (imeBottom - sysBottom).coerceAtLeast(0)
                    binding.nextButton.translationY = -offset.toFloat()
                    return insets
                }
                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    // 최종 프레임에서 잔떨림 방지(보정)
                    val insets = ViewCompat.getRootWindowInsets(binding.nextButton) ?: return
                    val sysBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    val offset = (imeBottom - sysBottom).coerceAtLeast(0)
                    binding.nextButton.translationY = -offset.toFloat()
                }
            }
        )
    }
}