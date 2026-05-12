package com.example.uitvolunteermap.features.admin.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.admin.dashboard.domain.usecase.GetAdminDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getAdminDashboardStatsUseCase: GetAdminDashboardStatsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminDashboardUiState(
            username = sessionManager.currentUsername?.takeIf { it.isNotBlank() }
                ?: "Quản trị viên"
        )
    )
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        onEvent(AdminDashboardUiEvent.Refresh)
    }

    fun onEvent(event: AdminDashboardUiEvent) {
        when (event) {
            AdminDashboardUiEvent.Refresh -> loadStats()
        }
    }

    private fun loadStats() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getAdminDashboardStatsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        current.copy(
                            accountCount = result.data.accountCount,
                            campaignCount = result.data.campaignCount,
                            teamCount = result.data.teamCount,
                            postCount = result.data.postCount,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }
}
