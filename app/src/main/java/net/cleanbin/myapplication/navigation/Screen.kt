package net.cleanbin.myapplication.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Result : Screen("result")
}
