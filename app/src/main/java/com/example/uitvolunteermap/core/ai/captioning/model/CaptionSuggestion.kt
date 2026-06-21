package com.example.uitvolunteermap.core.ai.captioning.model

data class CaptionSuggestion(
    val title: String,
    val content: String,
    val hashtags: List<String>,
    val perPhotoCaptions: List<String>,
    val rawLabels: List<String> = emptyList(),
    val refinedByLlm: Boolean = false
) {
    val contentWithHashtags: String
        get() = if (hashtags.isEmpty()) content else "$content\n\n${hashtags.joinToString(" ")}"
}
