package com.puffless.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.puffless.app.viewmodel.PuffViewModel

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Stats : Screen("stats")
    object Planner : Screen("planner")
}

@Composable
fun PuffNavGraph(
    navController: NavHostController,
    viewModel: PuffViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateStats = {
                    viewModel.loadRecentStats()
                    navController.navigate(Screen.Stats.route)
                },
                onNavigatePlanner = {
                    viewModel.loadRecentStats()
                    navController.navigate(Screen.Planner.route)
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Planner.route) {
            PlannerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}