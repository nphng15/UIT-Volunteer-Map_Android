package com.example.uitvolunteermap.features.admin.campaign.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.admin.campaign.domain.entity.AdminCampaign
import com.example.uitvolunteermap.features.admin.campaign.domain.usecase.GetAdminCampaignsUseCase
import com.example.uitvolunteermap.features.admin.campaign.domain.usecase.ManageAdminCampaignUseCase
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
class AdminCampaignViewModel @Inject constructor(
    private val getAdminCampaignsUseCase: GetAdminCampaignsUseCase,
    private val manageAdminCampaignUseCase: ManageAdminCampaignUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminCampaignUiState(canManageCampaigns = sessionManager.canManageCampaigns)
    )
    val uiState: StateFlow<AdminCampaignUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AdminCampaignUiEffect>()
    val uiEffect: SharedFlow<AdminCampaignUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(AdminCampaignUiEvent.RefreshRequested)
    }

    fun onEvent(event: AdminCampaignUiEvent) {
        when (event) {
            AdminCampaignUiEvent.RefreshRequested -> loadCampaigns(isPullRefresh = false)
            AdminCampaignUiEvent.PullToRefreshTriggered -> loadCampaigns(isPullRefresh = true)
            is AdminCampaignUiEvent.SearchQueryChanged ->
                _uiState.update { it.copy(searchQuery = event.query) }

            // Form
            AdminCampaignUiEvent.CreateClicked -> openCreateForm()
            is AdminCampaignUiEvent.EditClicked -> openEditForm(event.campaignId)
            AdminCampaignUiEvent.FormDismissed ->
                _uiState.update { it.copy(form = null) }
            is AdminCampaignUiEvent.FormNameChanged -> updateForm { it.copy(campaignName = event.value) }
            is AdminCampaignUiEvent.FormDescriptionChanged -> updateForm { it.copy(description = event.value) }
            is AdminCampaignUiEvent.FormStartDateChanged -> updateForm { it.copy(startDate = event.value) }
            is AdminCampaignUiEvent.FormEndDateChanged -> updateForm { it.copy(endDate = event.value) }
            is AdminCampaignUiEvent.FormLatitudeChanged -> updateForm { it.copy(latitude = event.value) }
            is AdminCampaignUiEvent.FormLongitudeChanged -> updateForm { it.copy(longitude = event.value) }
            is AdminCampaignUiEvent.FormCheckInRadiusChanged -> updateForm { it.copy(checkInRadius = event.value) }
            AdminCampaignUiEvent.FormSubmitted -> submitForm()

            // Delete
            is AdminCampaignUiEvent.DeleteClicked -> handleDeleteClicked(event.campaignId)
            AdminCampaignUiEvent.DeleteConfirmed -> handleDeleteConfirmed()
            AdminCampaignUiEvent.DeleteCancelled ->
                _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }

    // ─── Load ──────────────────────────────────────────────────────────────────

    private fun loadCampaigns(isPullRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val result = getAdminCampaignsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            campaigns = result.data.map(::toItemUiModel),
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    // ─── Form ────────────────────────────────────────────────────────────────────

    private fun openCreateForm() {
        if (!sessionManager.canManageCampaigns) return
        _uiState.update { it.copy(form = AdminCampaignFormState()) }
    }

    private fun openEditForm(campaignId: Int) {
        if (!sessionManager.canManageCampaigns) return
        val item = _uiState.value.campaigns.firstOrNull { it.campaignId == campaignId } ?: return
        _uiState.update {
            it.copy(
                form = AdminCampaignFormState(
                    editingId = item.campaignId,
                    campaignName = item.campaignName,
                    description = item.description,
                    startDate = item.startDate,
                    endDate = item.endDate,
                    latitude = item.latitude?.toString().orEmpty(),
                    longitude = item.longitude?.toString().orEmpty(),
                    checkInRadius = item.checkInRadius?.toString().orEmpty()
                )
            )
        }
    }

    private inline fun updateForm(transform: (AdminCampaignFormState) -> AdminCampaignFormState) {
        _uiState.update { state ->
            val form = state.form ?: return@update state
            // Xoá lỗi cũ khi người dùng tiếp tục nhập
            state.copy(form = transform(form).copy(errorMessage = null))
        }
    }

    private fun submitForm() {
        if (!sessionManager.canManageCampaigns) return
        val form = _uiState.value.form ?: return
        if (form.isSubmitting) return

        val latitude: Double?
        val longitude: Double?
        val checkInRadius: Double?
        if (form.isEditing) {
            try {
                latitude = form.latitude.toNullableDouble()
                longitude = form.longitude.toNullableDouble()
                checkInRadius = form.checkInRadius.toNullableDouble()
            } catch (e: NumberFormatException) {
                setFormError("Vĩ độ, kinh độ và bán kính phải là số hợp lệ.")
                return
            }
        } else {
            latitude = null
            longitude = null
            checkInRadius = null
        }

        _uiState.update { it.copy(form = form.copy(isSubmitting = true, errorMessage = null)) }

        viewModelScope.launch {
            val description = form.description.trim().ifBlank { null }
            val result = if (form.isEditing) {
                manageAdminCampaignUseCase.update(
                    campaignId = form.editingId!!,
                    campaignName = form.campaignName,
                    description = description,
                    startDate = form.startDate.trim(),
                    endDate = form.endDate.trim(),
                    latitude = latitude,
                    longitude = longitude,
                    checkInRadius = checkInRadius
                )
            } else {
                manageAdminCampaignUseCase.create(
                    campaignName = form.campaignName,
                    description = description,
                    startDate = form.startDate.trim(),
                    endDate = form.endDate.trim()
                )
            }

            when (result) {
                is AppResult.Success -> {
                    val isEditing = form.isEditing
                    _uiState.update { it.copy(form = null) }
                    _uiEffect.emit(
                        AdminCampaignUiEffect.ShowMessage(
                            if (isEditing) "Cập nhật chiến dịch thành công."
                            else "Tạo chiến dịch thành công."
                        )
                    )
                    loadCampaigns(isPullRefresh = false)
                }

                is AppResult.Error -> setFormError(result.error.userMessage)
            }
        }
    }

    private fun setFormError(message: String) {
        _uiState.update { state ->
            val form = state.form ?: return@update state
            state.copy(form = form.copy(isSubmitting = false, errorMessage = message))
        }
    }

    // ─── Delete ─────────────────────────────────────────────────────────────────

    private fun handleDeleteClicked(campaignId: Int) {
        if (!sessionManager.canManageCampaigns) return
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(pendingDeleteId = campaignId) }
    }

    private fun handleDeleteConfirmed() {
        if (!sessionManager.canManageCampaigns) return
        if (_uiState.value.isDeleting) return
        val campaignId = _uiState.value.pendingDeleteId ?: return

        _uiState.update { it.copy(pendingDeleteId = null, isDeleting = true) }

        viewModelScope.launch {
            when (val result = manageAdminCampaignUseCase.delete(campaignId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminCampaignUiEffect.ShowMessage("Xóa chiến dịch thành công."))
                    loadCampaigns(isPullRefresh = false)
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminCampaignUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────────

    private fun toItemUiModel(campaign: AdminCampaign): AdminCampaignItemUiModel =
        AdminCampaignItemUiModel(
            campaignId = campaign.campaignId,
            campaignName = campaign.campaignName,
            description = campaign.description ?: "",
            startDate = campaign.startDate,
            endDate = campaign.endDate,
            dateRange = "${campaign.startDate} – ${campaign.endDate}",
            latitude = campaign.latitude,
            longitude = campaign.longitude,
            checkInRadius = campaign.checkInRadius,
            hasLocation = campaign.latitude != null && campaign.longitude != null
        )

    /** "" → null; ngược lại parse Double, ném NumberFormatException nếu sai định dạng. */
    private fun String.toNullableDouble(): Double? =
        trim().takeIf { it.isNotEmpty() }?.toDouble()
}
