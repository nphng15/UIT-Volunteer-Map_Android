package com.example.uitvolunteermap.features.attendance.presentation

sealed interface AttendanceUiEvent {
    data object RefreshRequested : AttendanceUiEvent
    data object PullToRefreshTriggered : AttendanceUiEvent
    data class FilterChanged(val filter: AttendanceFilter) : AttendanceUiEvent
    data class TeamSelected(val teamId: Int) : AttendanceUiEvent
    data class DateSelected(val date: String) : AttendanceUiEvent
    data class MemberPhotoTapped(val userId: Int) : AttendanceUiEvent
    data object PhotoPreviewDismissed : AttendanceUiEvent
}
