package com.chewite.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chewite.app.ui.MainActivity
import com.chewite.app.ui.login.LoginActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        setSplashScreen(splash)
        super.onCreate(savedInstanceState)
        proceed()
    }

    private fun proceed() {
        val nextActivity = if (isLoggedIn()) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }
        startActivity(Intent(this, nextActivity))
        finish()
    }

    private fun setSplashScreen(splash: SplashScreen) {
        val MIN_DURATION = 1000L
        val start = System.currentTimeMillis()
        splash.setKeepOnScreenCondition {
            System.currentTimeMillis() - start < MIN_DURATION
        }
    }

    private fun isLoggedIn(): Boolean {
        return false
    }
}