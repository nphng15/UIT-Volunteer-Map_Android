package com.example.uitvolunteermap.features.campaign.presentation.form

sealed interface CampaignFormUiEvent {
    data class NameChanged(val value: String) : CampaignFormUiEvent
    data class DescriptionChanged(val value: String) : CampaignFormUiEvent
    data class StartDateChanged(val value: String) : CampaignFormUiEvent
    data class EndDateChanged(val value: String) : CampaignFormUiEvent

    data object SaveClicked : CampaignFormUiEvent

    // Back button / hardware back — sẽ hiện discard dialog nếu isDirty
    data object BackClicked : CampaignFormUiEvent

    // Dialog discard confirmation
    data object DiscardConfirmed : CampaignFormUiEvent
    data object DiscardCancelled : CampaignFormUiEvent
}
