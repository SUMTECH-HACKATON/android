package net.cleanbin.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    }
}
