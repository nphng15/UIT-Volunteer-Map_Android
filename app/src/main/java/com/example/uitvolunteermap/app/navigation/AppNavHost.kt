package com.example.uitvolunteermap.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignDetailRoute
import com.example.uitvolunteermap.features.campaign.presentation.form.CampaignFormRoute
import com.example.uitvolunteermap.features.campaign.presentation.list.CampaignListRoute
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamFormationDetailRoute
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerHomeRoute
import com.example.uitvolunteermap.features.home.presentation.HomeRoute
import com.example.uitvolunteermap.features.auth.presentation.LoginRoute
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupRoute
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsRoute

private const val AddPostResultKey = "add_post_result"
private const val CampaignFormResultKey = "campaign_form_result"

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Login.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 280)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 280)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 280)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 280)
            )
        }
    ) {
        composable(route = AppDestination.Login.route) {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigateSafely(AppDestination.Home.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppDestination.Home.route) {
            VolunteerHomeRoute(
                onOpenCampaignDetail = { campaignId ->
                    navController.navigateSafely(
                        AppDestination.CampaignDetail.createRoute(campaignId)
                    )
                },
                onOpenCampaignPosts = { campaignId ->
                    navController.navigateSafely(
                        AppDestination.CampaignPosts.createRoute(campaignId)
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
                onOpenCampaignPosts = { campaignId ->
                    navController.navigateSafely(
                        AppDestination.CampaignPosts.createRoute(campaignId)
                    )
                },
                onOpenTeamDetail = { teamId ->
                    navController.navigateSafely(
                        AppDestination.TeamFormationDetail.createRoute(teamId)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.CampaignPosts.route,
            arguments = listOf(
                navArgument(AppDestination.CampaignPosts.campaignIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            CampaignPostsRoute(
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigateSafely(AppDestination.Home.route) {
                        popUpTo(AppDestination.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
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
                    navController.navigateSafely(
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
                    navController.navigateSafely(
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
    }
}

private fun NavHostController.navigateSafely(
    route: String,
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {}
) {
    val currentEntry = currentBackStackEntry ?: return
    if (currentEntry.lifecycle.currentState != Lifecycle.State.RESUMED) return
    navigate(route, builder)
}
