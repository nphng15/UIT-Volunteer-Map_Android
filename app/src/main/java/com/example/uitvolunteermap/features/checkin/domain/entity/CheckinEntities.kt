package com.example.uitvolunteermap.features.checkin.domain.entity

data class CheckinResult(
    val checkInId: Int,
    val campaignId: Int,
    val distance: Double,
    val checkedInAt: String,
    val imageUrl: String? = null
)

data class CheckinHistoryItem(
    val checkInId: Int,
    val campaignId: Int,
    val latitude: Double,
    val longitude: Double,
    val distance: Double,
    val checkedInAt: String,
    val campaignName: String,
    val campaignLatitude: Double?,
    val campaignLongitude: Double?,
    val checkInRadius: Double?
)

/** Chiến dịch hiện tại của tình nguyện viên + trạng thái đã điểm danh chưa. */
data class MyCampaign(
    val campaignId: Int,
    val campaignName: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val latitude: Double?,
    val longitude: Double?,
    val checkInRadius: Double?,
    val teamId: Int?,
    val teamName: String?,
    val hasCheckedIn: Boolean,
    val checkedInAt: String?
) {
    val hasLocation: Boolean get() = latitude != null && longitude != null
}

/** Một ảnh khoảnh khắc trên wall của chiến dịch. */
data class CampaignMoment(
    val id: Int,
    val accId: Int,
    val imageUrl: String,
    val caption: String?,
    val isCheckinPhoto: Boolean,
    val createdAt: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val authorTeamName: String?
)
