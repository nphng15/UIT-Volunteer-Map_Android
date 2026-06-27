package com.example.uitvolunteermap.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.app.SessionEntryPoint
import com.example.uitvolunteermap.features.admin.account.presentation.AdminAccountRoute
import com.example.uitvolunteermap.features.admin.campaign.presentation.AdminCampaignRoute
import com.example.uitvolunteermap.features.admin.dashboard.presentation.AdminDashboardRoute
import com.example.uitvolunteermap.features.admin.post.presentation.AdminPostRoute
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamRoute
import com.example.uitvolunteermap.features.campaign.presentation.areamap.CampaignAreaMapRoute
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignDetailRoute
import com.example.uitvolunteermap.features.campaign.presentation.form.CampaignFormRoute
import com.example.uitvolunteermap.features.campaign.presentation.list.CampaignListRoute
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamFormationDetailRoute
import com.example.uitvolunteermap.features.checkin.presentation.hub.CheckinHubRoute
import com.example.uitvolunteermap.features.attendance.presentation.AttendanceRoute
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerHomeRoute
import com.example.uitvolunteermap.features.auth.presentation.LoginRoute
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupRoute
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsRoute
import com.example.uitvolunteermap.features.post.presentation.feed.FeedRoute
import com.example.uitvolunteermap.features.profile.presentation.ProfileRoute
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import dagger.hilt.android.EntryPointAccessors

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
                onLoginSuccess = { isAdmin ->
                    val target = if (isAdmin) {
                        AppDestination.AdminDashboard.route
                    } else {
                        AppDestination.Home.route
                    }
                    navController.navigateSafely(target) {
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
                onSeeAllCampaigns = {
                    navController.navigateSafely(AppDestination.CampaignList.route)
                },
                onTabSelected = { tab -> navController.navigateToVolunteerTab(tab) }
            )
        }

        // ===== Khu quản trị (ADMIN) =====
        composable(route = AppDestination.AdminDashboard.route) {
            val context = LocalContext.current
            AdminDashboardRoute(
                onTabSelected = { tab -> navController.navigateToAdminTab(tab) },
                onLogout = { navController.logoutToLogin(context) }
            )
        }
        composable(route = AppDestination.AdminAccounts.route) {
            val context = LocalContext.current
            AdminAccountRoute(
                onTabSelected = { tab -> navController.navigateToAdminTab(tab) },
                onLogout = { navController.logoutToLogin(context) }
            )
        }
        composable(route = AppDestination.AdminCampaigns.route) {
            val context = LocalContext.current
            AdminCampaignRoute(
                onTabSelected = { tab -> navController.navigateToAdminTab(tab) },
                onLogout = { navController.logoutToLogin(context) }
            )
        }
        composable(route = AppDestination.AdminTeams.route) {
            val context = LocalContext.current
            AdminTeamRoute(
                onTabSelected = { tab -> navController.navigateToAdminTab(tab) },
                onLogout = { navController.logoutToLogin(context) }
            )
        }
        composable(route = AppDestination.AdminPosts.route) {
            val context = LocalContext.current
            AdminPostRoute(
                onTabSelected = { tab -> navController.navigateToAdminTab(tab) },
                onLogout = { navController.logoutToLogin(context) }
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

        composable(route = AppDestination.Attendance.route) {
            AttendanceRoute(
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
                onOpenAreaMap = { campaignId ->
                    navController.navigateSafely(
                        AppDestination.CampaignAreaMap.createRoute(campaignId)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.CampaignAreaMap.route,
            arguments = listOf(
                navArgument(AppDestination.CampaignAreaMap.campaignIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            CampaignAreaMapRoute(
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
        VolunteerBottomBarTab.Manage -> AppDestination.Attendance.route
        VolunteerBottomBarTab.Me -> AppDestination.Profile.route
    }
    navigateSafely(route) {
        popUpTo(AppDestination.Home.route) { inclusive = false }
        launchSingleTop = true
    }
}

/**
 * Điều hướng giữa các tab gốc của khu quản trị. Mỗi tab là một điểm đến top-level:
 * pop về Dashboard rồi single-top để tránh chồng back stack.
 */
private fun NavHostController.navigateToAdminTab(tab: AdminBottomBarTab) {
    val route = when (tab) {
        AdminBottomBarTab.Dashboard -> AppDestination.AdminDashboard.route
        AdminBottomBarTab.Accounts -> AppDestination.AdminAccounts.route
        AdminBottomBarTab.Campaigns -> AppDestination.AdminCampaigns.route
        AdminBottomBarTab.Teams -> AppDestination.AdminTeams.route
        AdminBottomBarTab.Posts -> AppDestination.AdminPosts.route
    }
    navigateSafely(route) {
        popUpTo(AppDestination.AdminDashboard.route) { inclusive = false }
        launchSingleTop = true
    }
}

/** Đăng xuất: xoá session rồi quay về Login, dọn sạch back stack. */
private fun NavHostController.logoutToLogin(context: android.content.Context) {
    EntryPointAccessors
        .fromApplication(context.applicationContext, SessionEntryPoint::class.java)
        .sessionManager()
        .clearSession()
    navigate(AppDestination.Login.route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
