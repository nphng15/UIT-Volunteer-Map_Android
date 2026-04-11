package com.example.uitvolunteermap.features.campaign.data.mapper

import com.example.uitvolunteermap.features.campaign.data.model.CampaignDto
import com.example.uitvolunteermap.features.campaign.domain.model.Campaign

fun CampaignDto.toDomain(): Campaign = Campaign(
    campaignId = campaignId,
    campaignName = campaignName,
    description = description,
    startDate = startDate,
    endDate = endDate
)
