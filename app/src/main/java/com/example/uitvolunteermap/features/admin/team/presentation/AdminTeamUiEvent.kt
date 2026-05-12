package com.example.uitvolunteermap.features.admin.team.presentation

sealed interface AdminTeamUiEvent {
    data object RefreshRequested : AdminTeamUiEvent
    data object PullToRefreshTriggered : AdminTeamUiEvent

    data class SearchQueryChanged(val query: String) : AdminTeamUiEvent

    // ── Tạo / Sửa ──────────────────────────────────────────────────────────────
    data object CreateClicked : AdminTeamUiEvent
    data class EditClicked(val teamId: Int) : AdminTeamUiEvent
    data object FormDismissed : AdminTeamUiEvent

    data class FormTeamNameChanged(val value: String) : AdminTeamUiEvent
    data class FormLeaderIdChanged(val value: String) : AdminTeamUiEvent
    data class FormCampaignIdChanged(val value: String) : AdminTeamUiEvent
    data class FormDescriptionChanged(val value: String) : AdminTeamUiEvent
    data class FormImageUrlChanged(val value: String) : AdminTeamUiEvent
    data object FormSubmitted : AdminTeamUiEvent

    // ── Xóa ────────────────────────────────────────────────────────────────────
    data class DeleteClicked(val teamId: Int) : AdminTeamUiEvent
    data object DeleteConfirmed : AdminTeamUiEvent
    data object DeleteCancelled : AdminTeamUiEvent
}
