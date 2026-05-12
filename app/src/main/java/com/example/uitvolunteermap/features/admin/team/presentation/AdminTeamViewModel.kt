package com.example.uitvolunteermap.features.admin.team.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
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
            is AdminTeamUiEvent.FormLeaderIdChanged ->
                updateForm { it.copy(leaderId = event.value.filter(Char::isDigit)) }
            is AdminTeamUiEvent.FormCampaignIdChanged ->
                updateForm { it.copy(campaignId = event.value.filter(Char::isDigit)) }
            is AdminTeamUiEvent.FormDescriptionChanged ->
                updateForm { it.copy(description = event.value) }
            is AdminTeamUiEvent.FormImageUrlChanged ->
                updateForm { it.copy(imageUrl = event.value) }
            AdminTeamUiEvent.FormSubmitted -> submitForm()

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
        memberCount = team.memberCount
    )

    // ─── Form (tạo / sửa) ───────────────────────────────────────────────────────

    private fun openCreateForm() {
        if (!sessionManager.canManageCampaigns) return
        _uiState.update {
            it.copy(formState = AdminTeamFormState(mode = AdminTeamFormMode.Create))
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

    private fun submitForm() {
        val form = _uiState.value.formState ?: return
        if (form.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(formState = form.copy(isSubmitting = true)) }

            val result = when (form.mode) {
                AdminTeamFormMode.Create -> manageAdminTeamUseCase.create(
                    teamName = form.teamName,
                    // leaderId/campaignId là Int bắt buộc. Nhập rỗng → -1 để fail validation rõ ràng.
                    // TODO: thay ô nhập số bằng picker chọn leader (GET danh sách user theo role)
                    //       và picker chọn campaign (GET /campaigns) khi có nguồn dữ liệu.
                    leaderId = form.leaderId.toIntOrNull() ?: -1,
                    campaignId = form.campaignId.toIntOrNull() ?: -1,
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
