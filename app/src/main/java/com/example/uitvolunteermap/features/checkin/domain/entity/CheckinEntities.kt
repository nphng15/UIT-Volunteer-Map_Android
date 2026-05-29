package com.example.uitvolunteermap.features.checkin.domain.entity

data class CheckinResult(
    val checkInId: Int,
    val campaignId: Int,
    val distance: Double,
    val checkedInAt: String
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
