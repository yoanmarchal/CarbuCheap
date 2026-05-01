package com.ym.carbucheap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ym.carbucheap.ui.navigation.NavGraph
import com.ym.carbucheap.ui.theme.CarbuCheapTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CarbuCheapTheme {
                NavGraph()
            }
        }
    }
}

