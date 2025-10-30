package net.cleanbin.myapplication.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Result : Screen("result")
    object Achievement : Screen("achievement")
    object Settings : Screen("settings")
}
