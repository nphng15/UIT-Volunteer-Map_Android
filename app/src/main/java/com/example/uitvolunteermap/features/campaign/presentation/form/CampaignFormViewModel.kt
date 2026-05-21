package com.example.uitvolunteermap.features.campaign.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.ManageCampaignUseCase
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
class CampaignFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCampaignUseCase: GetCampaignUseCase,
    private val manageCampaignUseCase: ManageCampaignUseCase
) : ViewModel() {

    // campaignId == NO_ID (-1) → Create mode; any positive int → Edit mode
    private val campaignId: Int =
        savedStateHandle[AppDestination.CampaignForm.campaignIdArg]
            ?: AppDestination.CampaignForm.NO_ID

    private val _uiState = MutableStateFlow(
        CampaignFormUiState(
            mode = if (campaignId > 0) CampaignFormMode.Edit else CampaignFormMode.Create
        )
    )
    val uiState: StateFlow<CampaignFormUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CampaignFormUiEffect>()
    val uiEffect: SharedFlow<CampaignFormUiEffect> = _uiEffect.asSharedFlow()

    init {
        // Edit mode: preload campaign data dưới dạng initial snapshot
        if (campaignId > 0) preloadCampaign()
    }

    fun onEvent(event: CampaignFormUiEvent) {
        when (event) {
            is CampaignFormUiEvent.NameChanged ->
                _uiState.update { it.copy(campaignName = event.value, errorMessage = null) }
            is CampaignFormUiEvent.DescriptionChanged ->
                _uiState.update { it.copy(description = event.value, errorMessage = null) }
            is CampaignFormUiEvent.StartDateChanged ->
                _uiState.update { it.copy(startDate = event.value, errorMessage = null) }
            is CampaignFormUiEvent.EndDateChanged ->
                _uiState.update { it.copy(endDate = event.value, errorMessage = null) }
            CampaignFormUiEvent.SaveClicked -> handleSave()
            CampaignFormUiEvent.BackClicked -> handleBack()
            CampaignFormUiEvent.DiscardConfirmed -> {
                _uiState.update { it.copy(showDiscardDialog = false) }
                emitEffect(CampaignFormUiEffect.NavigateBack)
            }
            CampaignFormUiEvent.DiscardCancelled ->
                _uiState.update { it.copy(showDiscardDialog = false) }
        }
    }

    // ─── Preload ────────────────────────────────────────────────────────────────

    private fun preloadCampaign() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPreload = true, errorMessage = null) }

            // Real: campaignApiService.getCampaign(token, campaignId).data!!
            when (val result = getCampaignUseCase(campaignId)) {
                is AppResult.Success -> {
                    val c = result.data
                    // Snapshot và form fields được set đồng thời — isDirty sẽ là false ngay sau preload
                    _uiState.update {
                        it.copy(
                            campaignName = c.campaignName,
                            description = c.description ?: "",
                            startDate = c.startDate,
                            endDate = c.endDate,
                            initialName = c.campaignName,
                            initialDescription = c.description ?: "",
                            initialStartDate = c.startDate,
                            initialEndDate = c.endDate,
                            isLoadingPreload = false
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoadingPreload = false, errorMessage = result.error.userMessage)
                    }
                }
            }
        }
    }

    // ─── Save ────────────────────────────────────────────────────────────────────

    private fun handleSave() {
        val state = _uiState.value

        // Guard Edit mode: không submit nếu không có thay đổi
        if (state.mode == CampaignFormMode.Edit && !state.isDirty) {
            emitEffect(CampaignFormUiEffect.ShowMessage("Chưa có thay đổi nào để lưu."))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val result = when (state.mode) {
                // Create: POST /campaigns
                // Real: manageCampaignUseCase.create(...) → gọi CampaignApiService.createCampaign
                // Conflict: AppError.Conflict khi API trả về 409 (tên đã tồn tại)
                CampaignFormMode.Create -> manageCampaignUseCase.create(
                    campaignName = state.campaignName,
                    description = state.description.ifBlank { null },
                    startDate = state.startDate,
                    endDate = state.endDate
                )

                // Edit: PUT /campaigns/:id — chỉ gửi các field đã thay đổi (partial update)
                // Real: manageCampaignUseCase.update(...) → gọi CampaignApiService.updateCampaign
                // Conflict: AppError.Conflict khi tên mới trùng với chiến dịch khác (409)
                CampaignFormMode.Edit -> manageCampaignUseCase.update(
                    campaignId = campaignId,
                    // takeIf: chỉ gửi field nếu thực sự thay đổi so với snapshot
                    campaignName = state.campaignName
                        .takeIf { it != state.initialName },
                    description = state.description
                        .takeIf { it != state.initialDescription }
                        ?.ifBlank { null },       // empty string → null (xóa description)
                    startDate = state.startDate
                        .takeIf { it != state.initialStartDate },
                    endDate = state.endDate
                        .takeIf { it != state.initialEndDate }
                )
            }

            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    val msg = when (state.mode) {
                        CampaignFormMode.Create -> "Tạo chiến dịch thành công."
                        CampaignFormMode.Edit -> "Cập nhật chiến dịch thành công."
                    }
                    // FormSaved: Route sẽ truyền message về màn trước qua SavedStateHandle
                    emitEffect(CampaignFormUiEffect.FormSaved(msg))
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = result.error.userMessage)
                    }
                }
            }
        }
    }

    // ─── Back / Discard ─────────────────────────────────────────────────────────

    private fun handleBack() {
        if (_uiState.value.isDirty) {
            // Có thay đổi chưa lưu → hỏi xác nhận trước khi thoát
            _uiState.update { it.copy(showDiscardDialog = true) }
        } else {
            emitEffect(CampaignFormUiEffect.NavigateBack)
        }
    }

    private fun emitEffect(effect: CampaignFormUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }
}
