package com.example.uitvolunteermap.features.team.presentation.edit

import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.team.domain.usecase.ManageTeamUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Thêm import này
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class TeamEditViewModel @Inject constructor(
    // Thay đổi UpdateTeamUseCase thành ManageTeamUseCase
    private val manageTeamUseCase: ManageTeamUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamEditUiState())
    val uiState = _uiState.asStateFlow()

    // Lấy teamId được truyền từ route "team_edit/{teamId}"
    private val teamId: Int = savedStateHandle.get<Int>("teamId") ?: 0

    init {
        // Vừa mở màn hình là đi lấy data ngay
        loadTeamDetail()
    }

    private fun loadTeamDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Ở đây Hiền có thể dùng chung hàm getTeams từ Repo rồi lọc ra team đúng ID
            // Hoặc nếu ManageTeamUseCase có hàm getTeamById thì dùng luôn
            // Giả lập logic lấy data:
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
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(teamName = newName) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc) }
    }

    // Nếu Hiền có xử lý chọn ảnh, hãy thêm hàm này để update UI State
    fun onImageUrlChange(newUrl: String?) {
        _uiState.update { it.copy(imageUrl = newUrl) }
    }

    fun updateTeam(teamId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Gọi hàm updateTeam từ ManageTeamUseCase với đầy đủ 5 tham số (gồm cả role)
            val result = manageTeamUseCase.updateTeam(
                role = UserRole.VOLUNTEER, // Giả sử mặc định là Volunteer vì đây là màn hình Edit của Leader
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