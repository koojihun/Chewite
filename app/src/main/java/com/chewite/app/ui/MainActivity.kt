package com.chewite.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chewite.app.data.model.LoginStatus
import com.chewite.app.ui.theme.ChewiteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.loginStatus.value == LoginStatus.NOT_INIT
        }
        enableEdgeToEdge()
        setContent {
            ChewiteTheme {
                val loginStatus by mainViewModel.loginStatus.collectAsStateWithLifecycle()
                if (loginStatus != LoginStatus.NOT_INIT) {
                    key(loginStatus) { AppNavigation(loginStatus) }
                }
            }
        }
    }
}