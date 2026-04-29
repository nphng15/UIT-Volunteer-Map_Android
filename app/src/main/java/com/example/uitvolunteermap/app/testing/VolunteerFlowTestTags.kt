package com.example.uitvolunteermap.app.testing

object VolunteerFlowTestTags {
    const val LoginScreen = "login_screen"
    const val LoginEmailField = "login_email_field"
    const val LoginPasswordField = "login_password_field"
    const val LoginSubmitButton = "login_submit_button"

    const val VolunteerHomeScreen = "volunteer_home_screen"
    fun volunteerHomeCampaignPrimaryAction(campaignId: Int): String =
        "volunteer_home_campaign_primary_$campaignId"

    const val CampaignDetailScreen = "campaign_detail_screen"
    const val CampaignDetailList = "campaign_detail_list"
    const val CampaignDetailViewAllPosts = "campaign_detail_view_all_posts"
    fun campaignDetailTeamCard(teamId: Int): String = "campaign_detail_team_$teamId"

    const val TeamFormationDetailScreen = "team_formation_detail_screen"
    const val TeamFormationAddActivity = "team_formation_add_activity"

    const val AddPostPopupScreen = "add_post_popup_screen"
    const val AddPostTitleField = "add_post_title_field"
    const val AddPostContentField = "add_post_content_field"
    const val AddPostUploadButton = "add_post_upload_button"
    const val AddPostPublishButton = "add_post_publish_button"

    const val CampaignPostsScreen = "campaign_posts_screen"

    const val FeedScreen = "feed_screen"

    const val CheckinHubScreen = "checkin_hub_screen"
    const val CheckinShutterButton = "checkin_shutter_button"

    const val AttendanceScreen = "attendance_screen"
    const val AttendanceList = "attendance_list"
    fun attendanceMemberCard(userId: Int): String = "attendance_member_$userId"
}
