package com.example.uitvolunteermap.features.admin.account.presentation

sealed interface AdminAccountUiEvent {
    data object Refresh : AdminAccountUiEvent
    data object PullToRefresh : AdminAccountUiEvent

    data class SearchQueryChanged(val query: String) : AdminAccountUiEvent
    data class RoleFilterChanged(val role: AccountRole?) : AdminAccountUiEvent

    // ─── Create ────────────────────────────────────────────────────────────
    data object CreateClicked : AdminAccountUiEvent
    data object CreateDismissed : AdminAccountUiEvent
    data class CreateFullnameChanged(val value: String) : AdminAccountUiEvent
    data class CreateMssvChanged(val value: String) : AdminAccountUiEvent
    data class CreateClassChanged(val value: String) : AdminAccountUiEvent
    data class CreateEmailChanged(val value: String) : AdminAccountUiEvent
    data class CreateTeamIdChanged(val value: String) : AdminAccountUiEvent
    data class CreatePhoneChanged(val value: String) : AdminAccountUiEvent
    data class CreateUsernameChanged(val value: String) : AdminAccountUiEvent
    data class CreatePasswordChanged(val value: String) : AdminAccountUiEvent
    data class CreateRoleChanged(val role: AccountRole) : AdminAccountUiEvent
    data object CreateSubmitted : AdminAccountUiEvent

    // ─── Edit ──────────────────────────────────────────────────────────────
    data class EditClicked(val accId: Int) : AdminAccountUiEvent
    data object EditDismissed : AdminAccountUiEvent
    data class EditPasswordChanged(val value: String) : AdminAccountUiEvent
    data class EditRoleChanged(val role: AccountRole) : AdminAccountUiEvent
    data object EditSubmitted : AdminAccountUiEvent

    // ─── Delete ────────────────────────────────────────────────────────────
    data class DeleteClicked(val accId: Int) : AdminAccountUiEvent
    data object DeleteConfirmed : AdminAccountUiEvent
    data object DeleteCancelled : AdminAccountUiEvent
}
