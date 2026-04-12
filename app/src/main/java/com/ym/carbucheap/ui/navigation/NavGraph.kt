package com.ym.carbucheap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ym.carbucheap.ui.screen.DashboardScreen
import com.ym.carbucheap.ui.screen.SplashScreen
import com.ym.carbucheap.ui.viewmodel.DashboardViewModel

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onPermissionGranted = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(viewModel = viewModel)
        }
    }
}

