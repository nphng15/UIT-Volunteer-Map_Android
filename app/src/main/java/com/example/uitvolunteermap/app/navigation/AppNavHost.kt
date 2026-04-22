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
import com.example.uitvolunteermap.features.campaign.presentation.form.CampaignFormRoute
import com.example.uitvolunteermap.features.campaign.presentation.list.CampaignListRoute
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamFormationDetailRoute
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerHomeRoute
import com.example.uitvolunteermap.features.home.presentation.HomeRoute
import com.example.uitvolunteermap.features.auth.presentation.LoginRoute
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.uitvolunteermap.features.team.presentation.TeamListRoute

// Thêm import cho Screen và ViewModel mới của Hiền
import com.example.uitvolunteermap.features.team.presentation.TeamEditScreen
import com.example.uitvolunteermap.features.team.presentation.edit.TeamEditViewModel

private const val AddPostResultKey = "add_post_result"
private const val CampaignFormResultKey = "campaign_form_result"

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "team_edit/1"
        //startDestination = AppDestination.Login.route
    ) {
        composable(route = AppDestination.Login.route) {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
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

        composable(route = AppDestination.CampaignList.route) {
            CampaignListRoute(
                onOpenCampaignDetail = { campaignId ->
                    navController.navigate(
                        AppDestination.CampaignDetail.createRoute(campaignId)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.CampaignForm.route,
            arguments = listOf(
                navArgument(AppDestination.CampaignForm.campaignIdArg) {
                    type = NavType.IntType
                    defaultValue = AppDestination.CampaignForm.NO_ID
                }
            )
        ) { backStackEntry ->
            CampaignFormRoute(
                onSaved = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(CampaignFormResultKey, message)
                    navController.popBackStack()
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

        composable(route = AppDestination.TeamList.route) {
            TeamListRoute(
                viewModel = hiltViewModel(), // Hilt tự tạo ViewModel cho Hiền
                onTeamClick = { teamId ->
                    // Điều hướng sang màn hình chi tiết team dựa trên teamIdArg
                    navController.navigate(
                        AppDestination.TeamFormationDetail.createRoute(teamId)
                    )
                }
            )
        }

        composable(
            route = "team_edit/{teamId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            val viewModel: TeamEditViewModel = hiltViewModel()

            TeamEditScreen(
                teamId = teamId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
