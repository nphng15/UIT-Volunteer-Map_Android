package com.example.uitvolunteermap.features.checkin.presentation.hub

import java.io.File

sealed interface CheckinHubUiEvent {
    data object RefreshRequested : CheckinHubUiEvent
    data object PermissionGranted : CheckinHubUiEvent
    data object PermissionDenied : CheckinHubUiEvent
    data class LocationReceived(val latitude: Double, val longitude: Double) : CheckinHubUiEvent
    /** Người dùng vừa chụp xong một tấm ảnh (file local). */
    data class PhotoCaptured(val file: File) : CheckinHubUiEvent
    data object SuccessOverlayDismissed : CheckinHubUiEvent
    data class DeleteMomentRequested(val momentId: Int) : CheckinHubUiEvent
}
