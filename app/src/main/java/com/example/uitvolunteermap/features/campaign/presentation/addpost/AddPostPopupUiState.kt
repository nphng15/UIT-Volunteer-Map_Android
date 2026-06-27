package com.example.uitvolunteermap.features.campaign.presentation.addpost

import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode
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
    val regenerateNonce: Int = 0,
    val captionMode: CaptionMode = CaptionMode.TEMPLATE_FAST,
    val gemmaModelAvailable: Boolean = false,
    val campaignNameInput: String = ""
) {
    val attachmentDisplayNames: List<String>
        get() = pickedImages.map { it.fileName }
}
