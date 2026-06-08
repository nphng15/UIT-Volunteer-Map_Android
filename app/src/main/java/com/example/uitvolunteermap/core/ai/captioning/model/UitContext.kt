package com.example.uitvolunteermap.core.ai.captioning.model

data class UitContext(
    val campaignName: String? = null,
    val teamName: String? = null,
    val programName: String? = null,
    val placeName: String? = null,
    /** Real description of what the team does — used to enrich the caption. */
    val teamDescription: String? = null,
    /** Human-friendly date pulled from the photo's EXIF (e.g. "ngày 24/06/2026"). */
    val dateLabel: String? = null
) {
    val resolvedProgram: String
        get() = programName?.takeIf { it.isNotBlank() }
            ?: campaignName?.takeIf { it.isNotBlank() }
            ?: "Hoạt động tình nguyện UIT"

    val resolvedTeam: String
        get() = teamName?.takeIf { it.isNotBlank() } ?: "Đội hình tình nguyện UIT"

    companion object {
        val Default = UitContext()
    }
}
