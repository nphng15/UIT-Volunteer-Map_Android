package com.example.uitvolunteermap.features.admin.team.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.admin.account.domain.entity.Account
import com.example.uitvolunteermap.features.admin.account.domain.usecase.GetAccountsUseCase
import com.example.uitvolunteermap.features.admin.campaign.domain.entity.AdminCampaign
import com.example.uitvolunteermap.features.admin.campaign.domain.usecase.GetAdminCampaignsUseCase
import com.example.uitvolunteermap.features.admin.team.domain.entity.AdminTeam
import com.example.uitvolunteermap.features.admin.team.domain.usecase.GetAdminTeamsUseCase
import com.example.uitvolunteermap.features.admin.team.domain.usecase.ManageAdminTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminTeamViewModel @Inject constructor(
    private val getAdminTeamsUseCase: GetAdminTeamsUseCase,
    private val manageAdminTeamUseCase: ManageAdminTeamUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAdminCampaignsUseCase: GetAdminCampaignsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Quản lý team (tạo/sửa/xóa) chỉ dành cho admin — tái dùng cờ canManageCampaigns
    // (cùng nghĩa "ADMIN"). LEADER chỉ sửa được team của mình ở luồng khác, không thuộc màn admin này.
    private val _uiState = MutableStateFlow(
        AdminTeamUiState(canManageTeams = sessionManager.canManageCampaigns)
    )
    val uiState: StateFlow<AdminTeamUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AdminTeamUiEffect>()
    val uiEffect: SharedFlow<AdminTeamUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(AdminTeamUiEvent.RefreshRequested)
    }

    fun onEvent(event: AdminTeamUiEvent) {
        when (event) {
            AdminTeamUiEvent.RefreshRequested -> loadTeams(isPullRefresh = false)
            AdminTeamUiEvent.PullToRefreshTriggered -> loadTeams(isPullRefresh = true)

            is AdminTeamUiEvent.SearchQueryChanged ->
                _uiState.update { it.copy(searchQuery = event.query) }

            AdminTeamUiEvent.CreateClicked -> openCreateForm()
            is AdminTeamUiEvent.EditClicked -> openEditForm(event.teamId)
            AdminTeamUiEvent.FormDismissed -> dismissForm()

            is AdminTeamUiEvent.FormTeamNameChanged ->
                updateForm { it.copy(teamName = event.value) }
            is AdminTeamUiEvent.FormLeaderSelected ->
                updateForm { it.copy(selectedLeaderId = event.userId) }
            is AdminTeamUiEvent.FormCampaignSelected ->
                updateForm { it.copy(selectedCampaignId = event.campaignId) }
            is AdminTeamUiEvent.FormDescriptionChanged ->
                updateForm { it.copy(description = event.value) }
            is AdminTeamUiEvent.FormImageUrlChanged ->
                updateForm { it.copy(imageUrl = event.value) }
            AdminTeamUiEvent.FormSubmitted -> submitForm()

            is AdminTeamUiEvent.ManageMembersClicked -> openMemberForm(event.teamId)
            is AdminTeamUiEvent.MemberToggled -> toggleMember(event.userId)
            AdminTeamUiEvent.AddMemberSubmitted -> submitAddMember()
            AdminTeamUiEvent.MemberFormDismissed -> dismissMemberForm()

            is AdminTeamUiEvent.DeleteClicked -> handleDeleteClicked(event.teamId)
            AdminTeamUiEvent.DeleteConfirmed -> handleDeleteConfirmed()
            AdminTeamUiEvent.DeleteCancelled ->
                _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }

    // ─── Load ─────────────────────────────────────────────────────────────────────

    private fun loadTeams(isPullRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val result = getAdminTeamsUseCase()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        teams = result.data.map(::toUiModel),
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }

                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.error.userMessage
                    )
                }
            }
        }
    }

    private fun toUiModel(team: AdminTeam): AdminTeamUiModel = AdminTeamUiModel(
        teamId = team.teamId,
        teamName = team.teamName,
        description = team.description.orEmpty(),
        imageUrl = team.imageUrl,
        leadersLabel = team.leaders.joinToString(", ") { it.fullName }
            .ifBlank { "Chưa có nhóm trưởng" },
        memberCount = team.memberCount,
        isCheckInConfigured = team.isCheckInConfigured
    )

    // ─── Form (tạo / sửa) ───────────────────────────────────────────────────────

    private fun openCreateForm() {
        if (!sessionManager.canManageCampaigns) return
        ensureFormOptionsLoaded()
        _uiState.update { state ->
            state.copy(
                formState = AdminTeamFormState(
                    mode = AdminTeamFormMode.Create,
                    selectedLeaderId = state.leaderOptions.firstOrNull()?.userId,
                    selectedCampaignId = state.campaignOptions.firstOrNull()?.campaignId
                )
            )
        }
    }

    private fun openEditForm(teamId: Int) {
        if (!sessionManager.canManageCampaigns) return
        val team = _uiState.value.teams.firstOrNull { it.teamId == teamId } ?: return
        _uiState.update {
            it.copy(
                formState = AdminTeamFormState(
                    mode = AdminTeamFormMode.Edit,
                    teamId = team.teamId,
                    teamName = team.teamName,
                    description = team.description,
                    imageUrl = team.imageUrl.orEmpty()
                )
            )
        }
    }

    private fun dismissForm() {
        if (_uiState.value.formState?.isSubmitting == true) return
        _uiState.update { it.copy(formState = null) }
    }

    private inline fun updateForm(transform: (AdminTeamFormState) -> AdminTeamFormState) {
        _uiState.update { state ->
            val form = state.formState ?: return@update state
            state.copy(formState = transform(form))
        }
    }

    private fun ensureFormOptionsLoaded() {
        val state = _uiState.value
        if (state.isLoadingFormOptions || (state.leaderOptions.isNotEmpty() && state.campaignOptions.isNotEmpty())) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFormOptions = true) }

            val accountsResult = getAccountsUseCase()
            val campaignsResult = getAdminCampaignsUseCase()

            val leaderOptions = when (accountsResult) {
                is AppResult.Success -> accountsResult.data
                    .filter { it.roleName.equals("leader", ignoreCase = true) && it.userId != null }
                    .map(::toLeaderOption)
                is AppResult.Error -> emptyList()
            }
            val volunteerOptions = when (accountsResult) {
                is AppResult.Success -> accountsResult.data
                    .filter { it.roleName.equals("volunteer", ignoreCase = true) && it.userId != null }
                    .map(::toMemberOption)
                is AppResult.Error -> emptyList()
            }
            val campaignOptions = when (campaignsResult) {
                is AppResult.Success -> campaignsResult.data.map(::toCampaignOption)
                is AppResult.Error -> emptyList()
            }

            _uiState.update { current ->
                current.copy(
                    leaderOptions = leaderOptions,
                    volunteerOptions = volunteerOptions,
                    campaignOptions = campaignOptions,
                    isLoadingFormOptions = false,
                    formState = current.formState?.let { form ->
                        if (form.mode != AdminTeamFormMode.Create) form else form.copy(
                            selectedLeaderId = form.selectedLeaderId ?: leaderOptions.firstOrNull()?.userId,
                            selectedCampaignId = form.selectedCampaignId ?: campaignOptions.firstOrNull()?.campaignId
                        )
                    },
                    memberFormState = current.memberFormState
                )
            }

            if (accountsResult is AppResult.Error) {
                _uiEffect.emit(AdminTeamUiEffect.ShowMessage(accountsResult.error.userMessage))
            }
            if (campaignsResult is AppResult.Error) {
                _uiEffect.emit(AdminTeamUiEffect.ShowMessage(campaignsResult.error.userMessage))
            }
        }
    }

    private fun toLeaderOption(account: Account): AdminTeamLeaderOption {
        val name = account.fullName?.takeIf { it.isNotBlank() } ?: account.username
        val subtitle = account.toOptionSubtitle(name)
        return AdminTeamLeaderOption(
            userId = requireNotNull(account.userId),
            displayName = name,
            subtitle = subtitle
        )
    }

    private fun toMemberOption(account: Account): AdminTeamMemberOption {
        val name = account.fullName?.takeIf { it.isNotBlank() } ?: account.username
        val subtitle = account.toOptionSubtitle(name)
        return AdminTeamMemberOption(
            userId = requireNotNull(account.userId),
            displayName = name,
            subtitle = subtitle
        )
    }

    private fun Account.toOptionSubtitle(displayName: String): String {
        return listOfNotNull(
            email?.takeIf { it.isNotBlank() },
            username.takeIf { it != displayName },
            "User #$userId",
            "Tài khoản #$accId"
        ).joinToString(" · ")
    }

    private fun toCampaignOption(campaign: AdminCampaign): AdminTeamCampaignOption = AdminTeamCampaignOption(
        campaignId = campaign.campaignId,
        name = campaign.campaignName,
        dateRange = "${campaign.startDate} → ${campaign.endDate}"
    )

    private fun submitForm() {
        val form = _uiState.value.formState ?: return
        if (form.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(formState = form.copy(isSubmitting = true)) }

            val result = when (form.mode) {
                AdminTeamFormMode.Create -> manageAdminTeamUseCase.create(
                    teamName = form.teamName,
                    leaderId = form.selectedLeaderId ?: -1,
                    campaignId = form.selectedCampaignId ?: -1,
                    description = form.description,
                    imageUrl = form.imageUrl
                )

                AdminTeamFormMode.Edit -> manageAdminTeamUseCase.update(
                    teamId = form.teamId ?: -1,
                    teamName = form.teamName,
                    description = form.description,
                    imageUrl = form.imageUrl.ifBlank { null }
                )
            }

            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(formState = null) }
                    val msg = if (form.mode == AdminTeamFormMode.Create)
                        "Tạo đội thành công." else "Cập nhật đội thành công."
                    _uiEffect.emit(AdminTeamUiEffect.ShowMessage(msg))
                    // POST/PUT trả raw entity khác shape GET → refetch để đồng bộ trạng thái.
                    loadTeams(isPullRefresh = false)
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(formState = it.formState?.copy(isSubmitting = false))
                    }
                    _uiEffect.emit(AdminTeamUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    // ─── Thành viên ───────────────────────────────────────────────────────────────

    private fun openMemberForm(teamId: Int) {
        if (!sessionManager.canManageCampaigns) return
        ensureFormOptionsLoaded()
        val team = _uiState.value.teams.firstOrNull { it.teamId == teamId } ?: return
        _uiState.update { state ->
            state.copy(
                memberFormState = AdminTeamMemberFormState(
                    teamId = team.teamId,
                    teamName = team.teamName
                )
            )
        }
    }

    private inline fun updateMemberForm(transform: (AdminTeamMemberFormState) -> AdminTeamMemberFormState) {
        _uiState.update { state ->
            val form = state.memberFormState ?: return@update state
            state.copy(memberFormState = transform(form))
        }
    }

    private fun dismissMemberForm() {
        if (_uiState.value.memberFormState?.isSubmitting == true) return
        _uiState.update { it.copy(memberFormState = null) }
    }

    private fun toggleMember(userId: Int) {
        updateMemberForm { form ->
            val selected = if (userId in form.selectedUserIds) {
                form.selectedUserIds - userId
            } else {
                form.selectedUserIds + userId
            }
            form.copy(selectedUserIds = selected, errorMessage = null)
        }
    }

    private fun submitAddMember() {
        val form = _uiState.value.memberFormState ?: return
        if (form.isSubmitting) return
        val userIds = form.selectedUserIds.toList()
        if (userIds.isEmpty()) {
            _uiState.update {
                it.copy(memberFormState = form.copy(errorMessage = "Hãy chọn ít nhất một tình nguyện viên."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(memberFormState = form.copy(isSubmitting = true, errorMessage = null)) }
            val firstError = userIds
                .map { userId -> manageAdminTeamUseCase.addMember(form.teamId, userId) }
                .firstOrNull { it is AppResult.Error } as? AppResult.Error
            if (firstError == null) {
                _uiState.update { it.copy(memberFormState = null) }
                _uiEffect.emit(AdminTeamUiEffect.ShowMessage("Đã gán ${userIds.size} tình nguyện viên vào đội."))
                loadTeams(isPullRefresh = false)
            } else {
                updateMemberForm { it.copy(isSubmitting = false, errorMessage = firstError.error.userMessage) }
            }
        }
    }

    // ─── Xóa ──────────────────────────────────────────────────────────────────────

    private fun handleDeleteClicked(teamId: Int) {
        if (!sessionManager.canManageCampaigns) return
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(pendingDeleteId = teamId) }
    }

    private fun handleDeleteConfirmed() {
        if (!sessionManager.canManageCampaigns) return
        if (_uiState.value.isDeleting) return
        val teamId = _uiState.value.pendingDeleteId ?: return

        _uiState.update { it.copy(pendingDeleteId = null, isDeleting = true) }

        viewModelScope.launch {
            when (val result = manageAdminTeamUseCase.delete(teamId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminTeamUiEffect.ShowMessage("Xóa đội thành công."))
                    loadTeams(isPullRefresh = false)
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminTeamUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }
}
