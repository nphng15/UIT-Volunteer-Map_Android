package com.example.uitvolunteermap.features.checkin.presentation.hub

import com.example.uitvolunteermap.features.checkin.domain.entity.CampaignMoment
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign
import java.io.File

enum class CheckinHubStage {
    Loading,        // đang tải chiến dịch của tôi
    NoCampaign,     // chưa được gán chiến dịch
    Error,          // lỗi tải
    Ready           // có chiến dịch -> hiển thị camera + wall
}

enum class CameraFacing { Back, Front }

/** Trạng thái cảm-xúc-vào-ra của vùng viewfinder. */
enum class ShutterMode {
    Live,           // đang xem live, có nút chụp
    Reviewing,      // vừa chụp xong, đang xem trước (Send | X | flip-disabled)
    Sending         // đang upload + gửi server
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
    /** Lấy vị trí thất bại (GPS lỗi / chưa có fix) — cho phép hiển thị nút "Thử lại". */
    val locationError: Boolean = false,
    /** Quyền camera đã được cấp chưa — chỉ bind viewfinder khi true. */
    val cameraPermissionGranted: Boolean = false,

    /** Camera đang dùng — back / front. */
    val cameraFacing: CameraFacing = CameraFacing.Back,

    /** Trạng thái viewfinder: live / preview / sending. */
    val shutterMode: ShutterMode = ShutterMode.Live,
    /** Ảnh vừa chụp đang xem trước (chỉ có ở Reviewing/Sending). */
    val previewFile: File? = null,

    val isLoadingMoments: Boolean = false,
    /** Tải danh sách khoảnh khắc thất bại (phân biệt với "chưa có ảnh"). */
    val momentsError: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessOverlay: Boolean = false,

    /** AccId của user hiện tại (để biết moment nào do mình đăng → cho phép xoá). */
    val currentAccId: Int = 0,

    /** Moment đang được mở fullscreen (null = đóng). */
    val viewingMomentId: Int? = null
) {
    val hasCheckedIn: Boolean get() = campaign?.hasCheckedIn == true

    /** Chiến dịch chưa cấu hình toạ độ GPS — không thể điểm danh theo vị trí. */
    val campaignHasNoLocation: Boolean
        get() = campaign != null && (campaign.latitude == null || campaign.longitude == null)

    /** Trong vùng cho phép điểm danh? (chỉ cần cho ảnh đầu — điểm danh chính thức) */
    val isWithinRadius: Boolean
        get() {
            val d = distanceMeters ?: return false
            val radius = campaign?.checkInRadius ?: 100.0
            return d <= radius
        }

    val isCapturing: Boolean get() = shutterMode == ShutterMode.Sending

    /**
     * Nút chụp có bấm được không (chỉ ở Live).
     * - Luôn cần quyền camera + đang ở trạng thái Ready, không đang chụp.
     * - Đã điểm danh: đăng khoảnh khắc (POST /photos) không cần vị trí.
     * - Chưa điểm danh: cần quyền vị trí và đang trong vùng cho phép.
     */
    val canCapture: Boolean
        get() = stage == CheckinHubStage.Ready &&
            shutterMode == ShutterMode.Live &&
            cameraPermissionGranted &&
            (hasCheckedIn || (locationPermissionGranted && isWithinRadius))

    /** Moment đang xem fullscreen, nếu có. */
    val viewingMoment: CampaignMoment?
        get() = viewingMomentId?.let { id -> moments.firstOrNull { it.id == id } }

    /** Người dùng có quyền xoá moment đang xem? (chủ ảnh — admin/leader để vòng sau). */
    val canDeleteViewingMoment: Boolean
        get() = viewingMoment?.let { it.accId == currentAccId } == true
}
