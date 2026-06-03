package com.example.uitvolunteermap.features.attendance.presentation

sealed interface AttendanceUiEffect {
    data class ShowMessage(val message: String) : AttendanceUiEffect
}
