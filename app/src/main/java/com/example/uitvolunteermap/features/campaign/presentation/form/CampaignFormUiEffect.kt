package com.example.uitvolunteermap.features.campaign.presentation.form

sealed interface CampaignFormUiEffect {
    // Lưu thành công — Route sẽ truyền message về màn trước qua SavedStateHandle
    data class FormSaved(val message: String) : CampaignFormUiEffect
    data object NavigateBack : CampaignFormUiEffect
    data class ShowMessage(val message: String) : CampaignFormUiEffect
}
