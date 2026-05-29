package com.example.uitvolunteermap.features.campaign.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.CardShape
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.PillShape
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenAccentPressed
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBorder
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurface
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurfaceVariant
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextSecondary

@Composable
internal fun CampaignDescriptionBlock(
    description: String,
    isExpanded: Boolean,
    onReadMore: () -> Unit
) {
    val displayedText = if (isExpanded || description.length <= 220) {
        description
    } else {
        description.take(220).trimEnd() + "..."
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .border(1.dp, ScreenBorder, CardShape)
            .background(ScreenSurface, CardShape)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "CHIẾN DỊCH",
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = displayedText,
            color = ScreenTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (isExpanded) "Rút gọn ↓" else "Đọc thêm ↓",
            color = ScreenAccentPressed,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .border(1.dp, ScreenBorder, PillShape)
                .background(ScreenSurfaceVariant, PillShape)
                .clickable(onClick = onReadMore)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
