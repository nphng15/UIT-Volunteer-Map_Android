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
    data class FormLeaderSelected(val userId: Int) : AdminTeamUiEvent
    data class FormCampaignSelected(val campaignId: Int) : AdminTeamUiEvent
    data class FormDescriptionChanged(val value: String) : AdminTeamUiEvent
    data class FormImageUrlChanged(val value: String) : AdminTeamUiEvent
    data object FormSubmitted : AdminTeamUiEvent

    // ── Thành viên ─────────────────────────────────────────────────────────────
    data class ManageMembersClicked(val teamId: Int) : AdminTeamUiEvent
    data class MemberToggled(val userId: Int) : AdminTeamUiEvent
    data object AddMemberSubmitted : AdminTeamUiEvent
    data object MemberFormDismissed : AdminTeamUiEvent

    // ── Xóa ────────────────────────────────────────────────────────────────────
    data class DeleteClicked(val teamId: Int) : AdminTeamUiEvent
    data object DeleteConfirmed : AdminTeamUiEvent
    data object DeleteCancelled : AdminTeamUiEvent
}
