package com.example.uitvolunteermap.features.campaign.presentation.addpost

import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.PickedImage

data class AddPostPopupUiState(
    val appName: String = "VolunteerMap",
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
