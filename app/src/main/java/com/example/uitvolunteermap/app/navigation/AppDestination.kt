package com.example.uitvolunteermap.app.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object CampaignDetail : AppDestination("campaign/{campaignId}") {
        const val campaignIdArg: String = "campaignId"

        fun createRoute(campaignId: Int): String = "campaign/$campaignId"
    }
    data object CampaignPosts : AppDestination("campaign/{campaignId}/posts") {
        const val campaignIdArg: String = "campaignId"

        fun createRoute(campaignId: Int): String = "campaign/$campaignId/posts"
    }
    data object TeamFormationDetail : AppDestination("team/{teamId}") {
        const val teamIdArg: String = "teamId"

        fun createRoute(teamId: Int): String = "team/$teamId"
    }
    data object AddPostPopup : AppDestination("team/{teamId}/add-post") {
        const val teamIdArg: String = "teamId"

        fun createRoute(teamId: Int): String = "team/$teamId/add-post"
    }
    data object CampaignList : AppDestination("campaigns")
    // campaignId = NO_ID (-1) → Create mode; bất kỳ int dương → Edit mode (preload)
    data object CampaignForm : AppDestination("campaign/form?campaignId={campaignId}") {
        const val campaignIdArg = "campaignId"
        const val NO_ID = -1
        fun createRoute(campaignId: Int = NO_ID): String = "campaign/form?campaignId=$campaignId"
    }
    data object Login : AppDestination("login")
    data object Register : AppDestination("register")
    data object Profile : AppDestination("profile")
}
