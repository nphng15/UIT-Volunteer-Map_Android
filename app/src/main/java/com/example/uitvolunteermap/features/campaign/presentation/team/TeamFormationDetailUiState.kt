package com.example.uitvolunteermap.features.campaign.presentation.team

import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.PickedImage

data class TeamFormationDetailUiState(
    val appName: String = "",
    val appSubtitle: String? = null,
    val title: String = "",
    val description: String = "",
    val heroCards: List<TeamHeroCardUiModel> = emptyList(),
    val leaders: List<TeamLeaderUiModel> = emptyList(),
    val activities: List<TeamActivityUiModel> = emptyList(),
    val addPostSheet: TeamAddPostSheetUiState? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isGuest: Boolean = true,
    val canManagePosts: Boolean = false,
    val canCheckin: Boolean = false
)

data class TeamHeroCardUiModel(
    val label: String,
    val isPrimary: Boolean
)

data class TeamLeaderUiModel(
    val id: Int,
    val initials: String,
    val role: String,
    val name: String
)

data class TeamActivityUiModel(
    val id: Int,
    val label: String,
    val isAddButton: Boolean
)

data class TeamAddPostSheetUiState(
    val title: String = "",
    val content: String = "",
    val pickedImages: List<PickedImage> = emptyList(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isGeneratingCaption: Boolean = false,
    val captionSuggestion: CaptionSuggestion? = null,
    val regenerateNonce: Int = 0
) {
    val attachmentDisplayNames: List<String>
        get() = pickedImages.map { it.fileName }
}
