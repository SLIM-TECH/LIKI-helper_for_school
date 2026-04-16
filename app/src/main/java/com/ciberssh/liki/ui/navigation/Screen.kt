package com.ciberssh.liki.ui.navigation

sealed class Screen(val route: String) {
    object Schedule : Screen("schedule")
    object Homework : Screen("homework")
    object Books : Screen("books")
    object AI : Screen("ai")
    object BellTimer : Screen("bell_timer")
    object Settings : Screen("settings")
}
