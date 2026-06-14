package com.example.uitvolunteermap.features.admin.campaign.presentation

sealed interface AdminCampaignUiEvent {
    data object RefreshRequested : AdminCampaignUiEvent
    data object PullToRefreshTriggered : AdminCampaignUiEvent

    data class SearchQueryChanged(val query: String) : AdminCampaignUiEvent

    // ─── Form ──────────────────────────────────────────────────────────────
    data object CreateClicked : AdminCampaignUiEvent
    data class EditClicked(val campaignId: Int) : AdminCampaignUiEvent
    data object FormDismissed : AdminCampaignUiEvent

    data class FormNameChanged(val value: String) : AdminCampaignUiEvent
    data class FormDescriptionChanged(val value: String) : AdminCampaignUiEvent
    data class FormStartDateChanged(val value: String) : AdminCampaignUiEvent
    data class FormEndDateChanged(val value: String) : AdminCampaignUiEvent
    data class FormLatitudeChanged(val value: String) : AdminCampaignUiEvent
    data class FormLongitudeChanged(val value: String) : AdminCampaignUiEvent
    data class FormCheckInRadiusChanged(val value: String) : AdminCampaignUiEvent
    data object FormSubmitted : AdminCampaignUiEvent

    // ─── Delete ────────────────────────────────────────────────────────────
    data class DeleteClicked(val campaignId: Int) : AdminCampaignUiEvent
    data object DeleteConfirmed : AdminCampaignUiEvent
    data object DeleteCancelled : AdminCampaignUiEvent
}
