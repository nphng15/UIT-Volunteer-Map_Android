package com.example.uitvolunteermap.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.features.campaign.presentation.addpost.AddPostPopupRoute
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignDetailRoute
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamFormationDetailRoute
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerHomeRoute

private const val AddPostResultKey = "add_post_result"

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
                onOpenTeamDetail = { teamId ->
                    navController.navigate(
                        AppDestination.TeamFormationDetail.createRoute(teamId)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.TeamFormationDetail.route,
            arguments = listOf(
                navArgument(AppDestination.TeamFormationDetail.teamIdArg) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val addPostResult = backStackEntry.savedStateHandle
                .getStateFlow<String?>(AddPostResultKey, null)
                .collectAsStateWithLifecycle()

            TeamFormationDetailRoute(
                onOpenAddPostPopup = { teamId ->
                    navController.navigate(
                        AppDestination.AddPostPopup.createRoute(teamId)
                    )
                },
                resultMessage = addPostResult.value,
                onResultMessageConsumed = {
                    backStackEntry.savedStateHandle.remove<String>(AddPostResultKey)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.AddPostPopup.route,
            arguments = listOf(
                navArgument(AppDestination.AddPostPopup.teamIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            AddPostPopupRoute(
                onPostPublished = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(AddPostResultKey, message)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
