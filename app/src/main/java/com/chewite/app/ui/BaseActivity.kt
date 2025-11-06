package com.chewite.app.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chewite.app.R
import kotlin.math.max

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setTopPadding() {
        var baseBottom = 0
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val topbar = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
            )
            val imeBottom  = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val bottomBar = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            baseBottom = bottomBar
            v.setPadding(v.paddingLeft, topbar.top, v.paddingRight, baseBottom)
            insets
        }
        val content = findViewById<View>(android.R.id.content)
        ViewCompat.setWindowInsetsAnimationCallback(
            content,
            object : WindowInsetsAnimationCompat.Callback(
                WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
            ) {
                override fun onProgress(insets: WindowInsetsCompat, running: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                    val ime = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    val barsBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    val extra = max(0, ime - barsBottom) // 키보드가 올라온 만큼만 추가
                    content.setPadding(content.paddingLeft, content.paddingTop, content.paddingRight, baseBottom + extra)
                    return insets
                }
            }
        )
    }
}