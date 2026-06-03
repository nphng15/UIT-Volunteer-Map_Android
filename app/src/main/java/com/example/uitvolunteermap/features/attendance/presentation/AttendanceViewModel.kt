package com.example.uitvolunteermap.features.attendance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.attendance.domain.entity.ManagedTeam
import com.example.uitvolunteermap.features.attendance.domain.entity.MemberAttendance
import com.example.uitvolunteermap.features.attendance.domain.entity.TeamAttendance
import com.example.uitvolunteermap.features.attendance.domain.usecase.GetManagedTeamsUseCase
import com.example.uitvolunteermap.features.attendance.domain.usecase.GetTeamAttendanceUseCase
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
class AttendanceViewModel @Inject constructor(
    private val getManagedTeamsUseCase: GetManagedTeamsUseCase,
    private val getTeamAttendanceUseCase: GetTeamAttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AttendanceUiEffect>()
    val uiEffect: SharedFlow<AttendanceUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(AttendanceUiEvent.RefreshRequested)
    }

    fun onEvent(event: AttendanceUiEvent) {
        when (event) {
            AttendanceUiEvent.RefreshRequested -> load(isPullRefresh = false)
            AttendanceUiEvent.PullToRefreshTriggered -> load(isPullRefresh = true)
            is AttendanceUiEvent.FilterChanged ->
                _uiState.update { it.copy(filter = event.filter) }
            is AttendanceUiEvent.TeamSelected -> selectTeam(event.teamId)
            is AttendanceUiEvent.DateSelected -> selectDate(event.date)
            is AttendanceUiEvent.MemberPhotoTapped -> showPhotoPreview(event.userId)
            AttendanceUiEvent.PhotoPreviewDismissed ->
                _uiState.update { it.copy(previewMember = null) }
        }
    }

    private fun load(isPullRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            // Lấy danh sách đội leader quản lý trước để biết có gì để filter.
            val managedTeams = when (val result = getManagedTeamsUseCase()) {
                is AppResult.Success -> result.data
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                    return@launch
                }
            }

            val selectedTeamId = _uiState.value.selectedTeamId
                ?: managedTeams.firstOrNull()?.teamId
            loadAttendance(managedTeams, selectedTeamId, _uiState.value.selectedDate)
        }
    }

    private fun selectTeam(teamId: Int) {
        if (teamId == _uiState.value.selectedTeamId) return
        _uiState.update { it.copy(selectedTeamId = teamId, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            loadAttendance(
                managedTeams = _uiState.value.managedTeams.toDomain(),
                selectedTeamId = teamId,
                date = _uiState.value.selectedDate
            )
        }
    }

    private fun selectDate(date: String) {
        if (date == _uiState.value.selectedDate) return
        _uiState.update { it.copy(selectedDate = date, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            loadAttendance(
                managedTeams = _uiState.value.managedTeams.toDomain(),
                selectedTeamId = _uiState.value.selectedTeamId,
                date = date
            )
        }
    }

    private suspend fun loadAttendance(
        managedTeams: List<ManagedTeam>,
        selectedTeamId: Int?,
        date: String?
    ) {
        when (val result = getTeamAttendanceUseCase(teamId = selectedTeamId, date = date)) {
            is AppResult.Success -> {
                val attendance = result.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null,
                        teamName = attendance.teamName,
                        campaignName = attendance.campaignName,
                        date = attendance.date,
                        managedTeams = managedTeams.map { team -> team.toUiModel() },
                        selectedTeamId = attendance.teamId,
                        members = attendance.members.map { member -> member.toUiModel() }
                    )
                }
            }

            is AppResult.Error -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.error.userMessage,
                        managedTeams = managedTeams.map { team -> team.toUiModel() }
                    )
                }
            }
        }
    }

    private fun showPhotoPreview(userId: Int) {
        val member = _uiState.value.members.firstOrNull { it.userId == userId } ?: return
        _uiState.update { it.copy(previewMember = member) }
    }
}

private fun ManagedTeam.toUiModel() = ManagedTeamUiModel(
    teamId = teamId,
    teamName = teamName,
    campaignName = campaignName
)

private fun List<ManagedTeamUiModel>.toDomain() = map {
    ManagedTeam(teamId = it.teamId, teamName = it.teamName, campaignId = null, campaignName = it.campaignName)
}

private fun MemberAttendance.toUiModel() = MemberAttendanceUiModel(
    userId = userId,
    fullName = fullName,
    mssv = mssv ?: "—",
    initials = fullName.toInitials(),
    hasCheckedIn = hasCheckedIn,
    checkedInAt = checkedInAt?.toClockLabel(),
    distanceMeters = distance,
    imageUrl = imageUrl
)

/** Lấy 2 chữ cái đầu của 2 từ cuối tên (vd "Nguyễn Văn A" → "VA"). */
private fun String.toInitials(): String {
    val parts = trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (parts.isEmpty()) return "?"
    return if (parts.size == 1) {
        parts[0].take(1).uppercase()
    } else {
        (parts[parts.size - 2].take(1) + parts[parts.size - 1].take(1)).uppercase()
    }
}

/** ISO timestamp ("2026-06-12T08:15:30.000Z") → "08:15". An toàn nếu format lạ. */
private fun String.toClockLabel(): String {
    val timePart = substringAfter('T', "")
    if (timePart.length < 5) return this
    return timePart.substring(0, 5)
}
