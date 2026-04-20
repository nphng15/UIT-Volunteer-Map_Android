package com.example.uitvolunteermap.app.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.Espresso.pressBack
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.testing.TestAddPostRepository
import com.example.uitvolunteermap.testing.TestAuthRepository
import com.example.uitvolunteermap.testing.TestCampaignDetailRepository
import com.example.uitvolunteermap.testing.TestPostRepository
import com.example.uitvolunteermap.testing.TestTeamFormationDetailRepository
import com.example.uitvolunteermap.testing.TestVolunteerHomeRepository
import com.example.uitvolunteermap.testing.VolunteerFlowRobot
import com.example.uitvolunteermap.testing.VolunteerFlowTestActivity
import com.example.uitvolunteermap.testing.VolunteerFlowTestHarness
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class VolunteerFlowSmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<VolunteerFlowTestActivity>()

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var authRepository: TestAuthRepository

    @Inject
    lateinit var volunteerHomeRepository: TestVolunteerHomeRepository

    @Inject
    lateinit var campaignDetailRepository: TestCampaignDetailRepository

    @Inject
    lateinit var teamFormationDetailRepository: TestTeamFormationDetailRepository

    @Inject
    lateinit var addPostRepository: TestAddPostRepository

    @Inject
    lateinit var postRepository: TestPostRepository

    private lateinit var testHarness: VolunteerFlowTestHarness
    private lateinit var robot: VolunteerFlowRobot

    @Before
    fun setUp() {
        hiltRule.inject()

        sessionManager.clearSession()
        authRepository.reset()
        volunteerHomeRepository.reset()
        campaignDetailRepository.reset()
        teamFormationDetailRepository.reset()
        addPostRepository.reset()
        postRepository.reset()

        testHarness = VolunteerFlowTestHarness(composeRule)
        robot = VolunteerFlowRobot(composeRule)
        testHarness.launchAppNavHost()
    }

    @Test
    fun app_starts_at_login_route() {
        assertEquals(AppDestination.Login.route, testHarness.navController.currentDestination?.route)
        robot.assertLoginDisplayed()
    }

    @Test
    fun successful_login_navigates_to_home_and_clears_login_from_back_stack() {
        robot.loginAsVolunteer()
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.VolunteerHomeScreen)

        assertEquals(AppDestination.Home.route, testHarness.navController.currentDestination?.route)
        composeRule.onNodeWithTag(VolunteerFlowTestTags.VolunteerHomeScreen).assertIsDisplayed()

        composeRule.runOnIdle {
            assertFalse(testHarness.navController.popBackStack())
            assertEquals(AppDestination.Home.route, testHarness.navController.currentDestination?.route)
        }
    }

    @Test
    fun volunteer_home_primary_action_opens_campaign_detail() {
        robot.loginAsVolunteer()
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.VolunteerHomeScreen)

        robot.openCampaignDetail(campaignId = 1)
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.CampaignDetailScreen)

        assertEquals(
            AppDestination.CampaignDetail.route,
            testHarness.navController.currentDestination?.route
        )
    }

    @Test
    fun campaign_detail_can_open_posts_and_team_add_post_roundtrip() {
        robot.loginAsVolunteer()
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.VolunteerHomeScreen)

        robot.openCampaignDetail(campaignId = 1)
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.CampaignDetailScreen)

        robot.openCampaignPosts()
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.CampaignPostsScreen)
        assertEquals(
            AppDestination.CampaignPosts.route,
            testHarness.navController.currentDestination?.route
        )

        pressBack()
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.CampaignDetailScreen)

        robot.openTeamDetail(teamId = 101)
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.TeamFormationDetailScreen)

        robot.openAddPostPopup()
        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.AddPostPopupScreen)

        robot.publishPost(
            title = "Cap nhat tu popup",
            content = "Noi dung bai viet tao tu flow test"
        )

        testHarness.waitUntilTagDisplayed(VolunteerFlowTestTags.TeamFormationDetailScreen)
        robot.assertSnackbarMessage("Bai viet da duoc tao thanh cong.")
        assertEquals(
            AppDestination.TeamFormationDetail.route,
            testHarness.navController.currentDestination?.route
        )
    }

    @Test
    fun extension_routes_remain_in_nav_graph() {
        composeRule.runOnIdle {
            testHarness.navController.navigate(AppDestination.CampaignList.route)
        }
        assertEquals(
            AppDestination.CampaignList.route,
            testHarness.navController.currentDestination?.route
        )

        composeRule.runOnIdle {
            testHarness.navController.navigate(AppDestination.CampaignForm.createRoute())
        }
        assertEquals(
            AppDestination.CampaignForm.route,
            testHarness.navController.currentDestination?.route
        )
    }
}
