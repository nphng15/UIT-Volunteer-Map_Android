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
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinRoute
import com.example.uitvolunteermap.features.checkin.presentation.hub.CheckinHubRoute
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerHomeRoute
import com.example.uitvolunteermap.features.auth.presentation.LoginRoute
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupRoute
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsRoute
import com.example.uitvolunteermap.features.post.presentation.feed.FeedRoute
import com.example.uitvolunteermap.features.profile.presentation.ProfileRoute
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab

private const val AddPostResultKey = NavResultKeys.ADD_POST_RESULT
private const val CampaignFormResultKey = NavResultKeys.CAMPAIGN_FORM_RESULT

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
                onTabSelected = { tab -> navController.navigateToVolunteerTab(tab) }
            )
        }

        composable(route = AppDestination.Feed.route) {
            FeedRoute(
                onTabSelected = { tab -> navController.navigateToVolunteerTab(tab) }
            )
        }

        composable(route = AppDestination.CheckinHub.route) {
            CheckinHubRoute(
                onTabSelected = { tab -> navController.navigateToVolunteerTab(tab) }
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
                onEditCampaign = { campaignId ->
                    navController.navigateSafely(
                        AppDestination.CampaignForm.createRoute(campaignId)
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
                    navController.navigateSafely(
                        AppDestination.AddPostPopup.createRoute(teamId)
                    )
                },
                onOpenGpsCheckin = { campaignId, campaignName, lat, lng, radius ->
                    navController.navigateSafely(
                        AppDestination.GpsCheckin.createRoute(campaignId, campaignName, lat, lng, radius)
                    )
                },
                resultMessage = addPostResult.value,
                onResultMessageConsumed = {
                    backStackEntry.savedStateHandle.remove<String>(AddPostResultKey)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = AppDestination.CampaignList.route) { backStackEntry ->
            CampaignListRoute(
                onOpenCampaignDetail = { campaignId ->
                    navController.navigateSafely(
                        AppDestination.CampaignDetail.createRoute(campaignId)
                    )
                },
                onCreateCampaign = {
                    navController.navigateSafely(
                        AppDestination.CampaignForm.createRoute()
                    )
                },
                savedStateHandle = backStackEntry.savedStateHandle,
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
                    val campaignListEntry = runCatching {
                        navController.getBackStackEntry(AppDestination.CampaignList.route)
                    }.getOrNull()
                    campaignListEntry?.savedStateHandle
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

        composable(route = AppDestination.Profile.route) {
            ProfileRoute(
                onNavigateToLogin = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab -> navController.navigateToVolunteerTab(tab) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.GpsCheckin.route,
            arguments = listOf(
                navArgument(AppDestination.GpsCheckin.campaignIdArg) {
                    type = NavType.IntType
                },
                navArgument(AppDestination.GpsCheckin.campaignNameArg) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(AppDestination.GpsCheckin.latArg) {
                    type = NavType.FloatType
                    defaultValue = 0f
                },
                navArgument(AppDestination.GpsCheckin.lngArg) {
                    type = NavType.FloatType
                    defaultValue = 0f
                },
                navArgument(AppDestination.GpsCheckin.radiusArg) {
                    type = NavType.FloatType
                    defaultValue = 100f
                }
            )
        ) {
            GpsCheckinRoute(
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

/**
 * Điều hướng giữa các tab gốc của tình nguyện viên. Mỗi tab là một điểm đến top-level:
 * pop về Home rồi single-top để tránh chồng back stack khi nhảy qua lại giữa các tab.
 */
private fun NavHostController.navigateToVolunteerTab(tab: VolunteerBottomBarTab) {
    val route = when (tab) {
        VolunteerBottomBarTab.Home -> AppDestination.Home.route
        VolunteerBottomBarTab.Feed -> AppDestination.Feed.route
        VolunteerBottomBarTab.Checkin -> AppDestination.CheckinHub.route
        VolunteerBottomBarTab.Me -> AppDestination.Profile.route
    }
    navigateSafely(route) {
        popUpTo(AppDestination.Home.route) { inclusive = false }
        launchSingleTop = true
    }
}
