package com.example.uitvolunteermap.features.campaign.presentation.form

enum class CampaignFormMode { Create, Edit }

data class CampaignFormUiState(
    val mode: CampaignFormMode = CampaignFormMode.Create,

    // --- Form fields (luôn là String, null description được lưu dưới dạng "") ---
    val campaignName: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",

    // --- Initial snapshot (set sau khi preload ở Edit mode) ---
    // Dirty tracking so sánh fields hiện tại với snapshot này.
    val initialName: String = "",
    val initialDescription: String = "",
    val initialStartDate: String = "",
    val initialEndDate: String = "",

    // --- Status ---
    val isLoadingPreload: Boolean = false,  // đang tải dữ liệu gốc (Edit mode)
    val isSubmitting: Boolean = false,      // đang gọi create/update API
    val showDiscardDialog: Boolean = false, // hiện dialog xác nhận huỷ khi có thay đổi chưa lưu
    val errorMessage: String? = null,       // lỗi validation, conflict, network…
) {
    /**
     * True nếu bất kỳ field nào khác với snapshot ban đầu.
     * Dùng để:
     *  - Chặn submit Edit khi không có thay đổi.
     *  - Kích hoạt dialog xác nhận khi bấm Back.
     */
    val isDirty: Boolean
        get() = campaignName != initialName ||
                description != initialDescription ||
                startDate != initialStartDate ||
                endDate != initialEndDate
}
