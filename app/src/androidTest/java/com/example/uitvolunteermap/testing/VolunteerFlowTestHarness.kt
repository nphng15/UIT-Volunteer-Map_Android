package com.example.uitvolunteermap.testing

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.uitvolunteermap.app.UITVolunteerTheme
import com.example.uitvolunteermap.app.navigation.AppNavHost
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags

class VolunteerFlowTestHarness(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<VolunteerFlowTestActivity>, VolunteerFlowTestActivity>,
) {
    lateinit var navController: TestNavHostController
        private set

    fun launchAppNavHost() {
        composeRule.setContent {
            val context = LocalContext.current
            navController = remember {
                TestNavHostController(context).apply {
                    navigatorProvider.addNavigator(ComposeNavigator())
                }
            }

            UITVolunteerTheme {
                AppNavHost(navController = navController)
            }
        }
        composeRule.waitForIdle()
    }

    fun waitUntilTagDisplayed(tag: String, timeoutMillis: Long = 5_000) {
        composeRule.waitUntil(timeoutMillis) {
            runCatching {
                composeRule.onNodeWithTag(tag).fetchSemanticsNode()
                true
            }.getOrElse { false }
        }
    }
}

class VolunteerFlowRobot(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<VolunteerFlowTestActivity>, VolunteerFlowTestActivity>,
) {
    fun assertLoginDisplayed() {
        composeRule.onNodeWithTag(VolunteerFlowTestTags.LoginScreen).assertIsDisplayed()
        composeRule.onNodeWithTag(VolunteerFlowTestTags.LoginSubmitButton).assertIsDisplayed()
    }

    fun loginAsVolunteer() {
        composeRule.onNodeWithTag(VolunteerFlowTestTags.LoginEmailField)
            .performTextInput("volunteer@uit.edu.vn")
        composeRule.onNodeWithTag(VolunteerFlowTestTags.LoginPasswordField)
            .performTextInput("volunteer123")
        composeRule.onNodeWithTag(VolunteerFlowTestTags.LoginSubmitButton)
            .performClick()
    }

    fun openCampaignDetail(campaignId: Int) {
        composeRule.onNodeWithTag(
            VolunteerFlowTestTags.volunteerHomeCampaignPrimaryAction(campaignId)
        ).performClick()
    }

    fun openCampaignPosts() {
        composeRule.onNodeWithTag(VolunteerFlowTestTags.CampaignDetailViewAllPosts)
            .performClick()
    }

    fun openTeamDetail(teamId: Int) {
        composeRule.onNodeWithTag(VolunteerFlowTestTags.campaignDetailTeamCard(teamId))
            .performClick()
    }

    fun openAddPostPopup() {
        composeRule.onNodeWithTag(VolunteerFlowTestTags.TeamFormationAddActivity)
            .performClick()
    }

    fun publishPost(title: String, content: String) {
        composeRule.onNodeWithTag(VolunteerFlowTestTags.AddPostTitleField)
            .performTextInput(title)
        composeRule.onNodeWithTag(VolunteerFlowTestTags.AddPostContentField)
            .performTextInput(content)
        composeRule.onNodeWithTag(VolunteerFlowTestTags.AddPostPublishButton)
            .performClick()
    }

    fun assertSnackbarMessage(message: String) {
        composeRule.onNodeWithText(message).assertIsDisplayed()
    }
}
