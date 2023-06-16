package com.faatikhriziq.paru.ui.navigation

sealed class Screen (val route: String){
    object Home: Screen("home")
    object Blog: Screen("blog")
    object History: Screen("history")
    object Explore: Screen("explore")
    object Capture: Screen("capture")
}