package com.chewite.app.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chewite.app.R
import kotlin.math.max

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemUI()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setTopPadding()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setTopPadding()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setTopPadding()
    }

    private fun setupSystemUI() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    abstract fun setTopPadding()
}