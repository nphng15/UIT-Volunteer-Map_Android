package com.example.uitvolunteermap.features.campaign.presentation.list

sealed interface CampaignListUiEvent {
    // Load lần đầu hoặc retry sau lỗi
    data object RefreshRequested : CampaignListUiEvent

    // Pull-to-refresh từ UI — dùng isRefreshing thay vì isLoading để giữ list hiện tại
    data object PullToRefreshTriggered : CampaignListUiEvent

    data class CampaignClicked(val campaignId: Int) : CampaignListUiEvent

    // User nhấn icon thùng rác → mở confirm dialog
    data class DeleteClicked(val campaignId: Int) : CampaignListUiEvent

    // User xác nhận xóa trong dialog → Optimistic delete
    data object DeleteConfirmed : CampaignListUiEvent

    // User hủy / đóng dialog
    data object DeleteCancelled : CampaignListUiEvent
}
