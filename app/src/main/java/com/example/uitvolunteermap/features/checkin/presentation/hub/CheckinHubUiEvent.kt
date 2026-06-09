package com.example.uitvolunteermap.features.checkin.presentation.hub

import java.io.File

sealed interface CheckinHubUiEvent {
    data object RefreshRequested : CheckinHubUiEvent
    data object PermissionGranted : CheckinHubUiEvent
    data object PermissionDenied : CheckinHubUiEvent

    /** Kết quả xin quyền camera (tách khỏi quyền vị trí). */
    data class CameraPermissionResult(val granted: Boolean) : CheckinHubUiEvent

    data class LocationReceived(val latitude: Double, val longitude: Double) : CheckinHubUiEvent
    /** Không lấy được vị trí (GPS lỗi / chưa có fix). */
    data object LocationUnavailable : CheckinHubUiEvent
    /** Người dùng yêu cầu lấy lại vị trí. */
    data object RetryLocationRequested : CheckinHubUiEvent

    /** Người dùng vừa chụp xong một tấm ảnh — chuyển sang trạng thái xem trước. */
    data class PhotoCaptured(val file: File) : CheckinHubUiEvent

    /** Bấm X huỷ ảnh đang xem trước — quay lại live camera. */
    data object PreviewDismissed : CheckinHubUiEvent

    /** Bấm Send — gửi ảnh đang xem trước (điểm danh hoặc đăng moment). */
    data object PreviewSendRequested : CheckinHubUiEvent

    /** Bấm flip — đổi giữa camera trước/sau (chỉ khi đang live preview). */
    data object CameraFlipRequested : CheckinHubUiEvent

    data object SuccessOverlayDismissed : CheckinHubUiEvent

    /** Tap vào một moment trong wall để mở fullscreen viewer. */
    data class MomentSelected(val momentId: Int) : CheckinHubUiEvent
    data object MomentViewerDismissed : CheckinHubUiEvent

    /** Xoá moment (chủ ảnh hoặc admin/leader). Backend cũng xoá khỏi Cloudinary. */
    data class DeleteMomentRequested(val momentId: Int) : CheckinHubUiEvent
}
