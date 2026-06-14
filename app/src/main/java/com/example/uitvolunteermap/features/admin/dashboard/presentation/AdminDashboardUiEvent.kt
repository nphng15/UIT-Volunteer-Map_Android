package com.example.uitvolunteermap.features.admin.dashboard.presentation

sealed interface AdminDashboardUiEvent {
    data object Refresh : AdminDashboardUiEvent
}
