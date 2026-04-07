package com.example.uitvolunteermap.app.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object CampaignDetail : AppDestination("campaign/{campaignId}") {
        const val campaignIdArg: String = "campaignId"

        fun createRoute(campaignId: Int): String = "campaign/$campaignId"
    }
    data object Login : AppDestination("login")
    data object Register : AppDestination("register")
    data object Profile : AppDestination("profile")
}
