package com.example.uitvolunteermap.features.admin.campaign.data.mapper

import com.example.uitvolunteermap.features.admin.campaign.data.model.AdminCampaignDto
import com.example.uitvolunteermap.features.admin.campaign.domain.entity.AdminCampaign

fun AdminCampaignDto.toDomain(): AdminCampaign = AdminCampaign(
    campaignId = campaignId,
    campaignName = campaignName,
    description = description,
    startDate = startDate,
    endDate = endDate,
    latitude = latitude,
    longitude = longitude,
    checkInRadius = checkInRadius
)
