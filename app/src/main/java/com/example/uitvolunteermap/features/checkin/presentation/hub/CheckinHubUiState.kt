package com.example.uitvolunteermap.features.checkin.presentation.hub

import com.example.uitvolunteermap.features.checkin.domain.entity.CampaignMoment
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign

enum class CheckinHubStage {
    Loading,        // đang tải chiến dịch của tôi
    NoCampaign,     // chưa được gán chiến dịch
    Error,          // lỗi tải
    Ready           // có chiến dịch -> hiển thị camera + wall
}

data class CheckinHubUiState(
    val stage: CheckinHubStage = CheckinHubStage.Loading,
    val campaign: MyCampaign? = null,
    val moments: List<CampaignMoment> = emptyList(),

    // Vị trí người dùng + khoảng cách tới điểm chiến dịch (mét).
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val distanceMeters: Double? = null,
    val locationPermissionGranted: Boolean = false,

    val isCapturing: Boolean = false,     // đang chụp/upload/gửi
    val isLoadingMoments: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessOverlay: Boolean = false
) {
    val hasCheckedIn: Boolean get() = campaign?.hasCheckedIn == true

    /** Trong vùng cho phép điểm danh? (chỉ cần cho ảnh đầu — điểm danh chính thức) */
    val isWithinRadius: Boolean
        get() {
            val d = distanceMeters ?: return false
            val radius = campaign?.checkInRadius ?: 100.0
            return d <= radius
        }

    /** Nút chụp có bấm được không. Ảnh đầu cần ở trong vùng; ảnh sau (đã điểm danh) thì tự do. */
    val canCapture: Boolean
        get() = stage == CheckinHubStage.Ready &&
            !isCapturing &&
            locationPermissionGranted &&
            (hasCheckedIn || isWithinRadius)
}
