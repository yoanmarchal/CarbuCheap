package com.ym.carbucheap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ym.carbucheap.ui.screen.DashboardScreen
import com.ym.carbucheap.ui.screen.SplashScreen
import com.ym.carbucheap.ui.viewmodel.DashboardViewModel
import kotlinx.serialization.Serializable

// Routes type-safe — Navigation Compose 2.8+ (kotlinx.serialization)
sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Dashboard : Route
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onPermissionGranted = {
                    navController.navigate(Route.Dashboard) {
                        popUpTo<Route.Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Dashboard> {
            DashboardScreen(viewModel = viewModel)
        }
    }
}
