package com.chewite.app.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemUI()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setSystemPaddings()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setSystemPaddings()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setSystemPaddings()
    }

    private fun setupSystemUI() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    abstract fun setSystemPaddings()
}