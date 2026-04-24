package com.example.uitvolunteermap.features.team.presentation.TeamList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.team.domain.usecase.GetTeamsUseCase
import com.example.uitvolunteermap.core.common.error.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamListViewModel @Inject constructor(
    private val getTeamsUseCase: GetTeamsUseCase
) : ViewModel() {

    // 1. Quản lý trạng thái dữ liệu (Teams, Loading, Error)
    private val _uiState = MutableStateFlow(TeamListUiState())
    val uiState = _uiState.asStateFlow()

    // 2. Quản lý hiệu ứng một lần (Điều hướng sang màn hình Detail)
    private val _uiEffect = MutableSharedFlow<TeamListUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        // Tự động tải dữ liệu khi khởi tạo ViewModel
        loadTeams()
    }

    /**
     * Cổng duy nhất xử lý mọi hành động từ UI
     */
    fun onEvent(event: TeamListUiEvent) {
        when (event) {
            is TeamListUiEvent.OnTeamClicked -> {
                viewModelScope.launch {
                    // Phát đi effect yêu cầu chuyển màn hình
                    _uiEffect.emit(TeamListUiEffect.NavigateToDetail(event.teamId))
                }
            }
            is TeamListUiEvent.OnRefresh -> {
                loadTeams()
            }
        }
    }

    /**
     * Logic tải danh sách team (chuyển về private để đảm bảo tính đóng gói)
     */
    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getTeamsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            teams = result.data,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }
}