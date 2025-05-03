package com.puffless.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.puffless.app.viewmodel.PuffViewModel

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Stats : Screen("stats")
    object Planner : Screen("planner")
    object Settings : Screen("settings")
}

@Composable
fun PuffNavGraph(
    navController: NavHostController,
    viewModel: PuffViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(viewModel = viewModel)
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Planner.route) {
            PlannerScreen(
                viewModel = viewModel,
                onBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
