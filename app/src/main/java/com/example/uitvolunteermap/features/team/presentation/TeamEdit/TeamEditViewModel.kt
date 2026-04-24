package com.example.uitvolunteermap.features.team.presentation.TeamEdit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.team.domain.usecase.ManageTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamEditViewModel @Inject constructor(
    private val manageTeamUseCase: ManageTeamUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Quản lý trạng thái UI
    private val _uiState = MutableStateFlow(TeamEditUiState())
    val uiState = _uiState.asStateFlow()

    // Quản lý các hiệu ứng một lần (như quay lại màn hình cũ)
    private val _uiEffect = MutableSharedFlow<TeamEditUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    // Lấy teamId từ NavGraph
    private val teamId: Int = savedStateHandle.get<Int>("teamId") ?: 0

    init {
        loadTeamDetail()
    }

    // Cổng duy nhất nhận hành động từ Screen
    fun onEvent(event: TeamEditUiEvent) {
        when (event) {
            is TeamEditUiEvent.OnNameChanged -> {
                _uiState.update { it.copy(teamName = event.newName) }
            }
            is TeamEditUiEvent.OnDescriptionChanged -> {
                _uiState.update { it.copy(description = event.newDesc) }
            }
            is TeamEditUiEvent.OnImageUrlChanged -> {
                _uiState.update { it.copy(imageUrl = event.newUrl) }
            }
            is TeamEditUiEvent.OnSaveClicked -> {
                updateTeam()
            }
            is TeamEditUiEvent.OnBackClicked -> {
                viewModelScope.launch { _uiEffect.emit(TeamEditUiEffect.NavigateBack) }
            }
        }
    }

    private fun loadTeamDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = manageTeamUseCase.getTeamById(teamId)

            if (result is AppResult.Success) {
                val team = result.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        teamName = team.name,
                        description = team.description,
                        imageUrl = team.imageUrl
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                // Có thể phát effect báo lỗi ở đây
            }
        }
    }

    private fun updateTeam() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // leader rule: a leader can only update their own team
            val result = manageTeamUseCase.updateTeam(
                role = UserRole.VOLUNTEER,
                id = teamId,
                name = _uiState.value.teamName,
                description = _uiState.value.description,
                imageUrl = _uiState.value.imageUrl
            )

            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isUpdateSuccess = true, errorMessage = null)
                    }
                    // Lưu xong thì phát tín hiệu quay về màn hình trước
                    _uiEffect.emit(TeamEditUiEffect.NavigateBack)
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Cập nhật thất bại: ${result.error}")
                    }
                }
            }
        }
    }
}