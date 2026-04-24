package com.example.uitvolunteermap.features.team.presentation.addattachment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.team.domain.usecase.AttachmentUpdate
import com.example.uitvolunteermap.features.team.domain.usecase.UpdateTeamAttachmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAttachmentViewModel @Inject constructor(
    private val updateAttachmentsUseCase: UpdateTeamAttachmentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAttachmentUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddAttachmentUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: AddAttachmentUiEvent) {
        when (event) {
            is AddAttachmentUiEvent.OnPhotosPicked -> handlePhotosPicked(event.uris)
            is AddAttachmentUiEvent.OnSave -> handleSave(1) // TeamId ví dụ
            is AddAttachmentUiEvent.OnDismiss -> {
                viewModelScope.launch { _uiEffect.emit(AddAttachmentUiEffect.NavigateBack) }
            }
            is AddAttachmentUiEvent.OnDeletePhoto -> { /* Logic xóa */ }
            else -> {println("Event chưa được hỗ trợ: $event")}
        }
    }

    private fun handlePhotosPicked(uris: List<Uri>) {
        val newAttachments = uris.map { LocalAttachment(uri = it, isLoading = true) }
        _uiState.update { it.copy(temporaryAttachments = it.temporaryAttachments + newAttachments) }
        newAttachments.forEach { simulateUpload(it.uri) }
    }

    private fun simulateUpload(uri: Uri) {
        viewModelScope.launch {
            delay(1000)
            _uiState.update { state ->
                val list = state.temporaryAttachments.map {
                    if (it.uri == uri) it.copy(isLoading = false, remoteUrl = "https://cdn.uit.edu.vn/${uri.lastPathSegment}.jpg")
                    else it
                }
                state.copy(temporaryAttachments = list)
            }
        }
    }

    private fun handleSave(teamId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val updates = _uiState.value.temporaryAttachments
                .filter { it.remoteUrl != null }
                .mapIndexed { index, item -> AttachmentUpdate(item.remoteUrl!!, index) }

            val result = updateAttachmentsUseCase(teamId, updates)
            if (result is AppResult.Success) {
                _uiEffect.emit(AddAttachmentUiEffect.NavigateBack)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.emit(AddAttachmentUiEffect.ShowError("Lỗi lưu ảnh"))
            }
        }
    }
}