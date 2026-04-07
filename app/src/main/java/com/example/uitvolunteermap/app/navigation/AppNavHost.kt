package com.example.uitvolunteermap.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignDetailRoute
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerHomeRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(route = AppDestination.Home.route) {
            VolunteerHomeRoute(
                onOpenCampaignDetail = { campaignId ->
                    navController.navigate(
                        AppDestination.CampaignDetail.createRoute(campaignId)
                    )
                }
            )
        }

        composable(
            route = AppDestination.CampaignDetail.route,
            arguments = listOf(
                navArgument(AppDestination.CampaignDetail.campaignIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            CampaignDetailRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
