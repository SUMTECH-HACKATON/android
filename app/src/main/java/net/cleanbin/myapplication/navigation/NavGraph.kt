package net.cleanbin.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.cleanbin.myapplication.ui.screen.AchievementScreen
import net.cleanbin.myapplication.ui.screen.HomeScreen
import net.cleanbin.myapplication.ui.screen.ResultScreen
import net.cleanbin.myapplication.ui.viewmodel.RecyclingViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: RecyclingViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route)
                },
                onNavigateToAchievements = {
                    navController.navigate(Screen.Achievement.route)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Achievement.route) {
            val achievements by viewModel.achievements.collectAsState()
            val totalAnalysisCount by viewModel.totalAnalysisCount.collectAsState()

            AchievementScreen(
                achievements = achievements,
                totalAnalysisCount = totalAnalysisCount,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
