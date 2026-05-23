package com.example.uitvolunteermap.features.campaign.presentation.detail.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.uitvolunteermap.core.ui.VolunteerTopBar
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextMuted

@Composable
internal fun DetailTopBar(
    routeLabel: String,
    onBackClick: () -> Unit
) {
    // Không có hành động phải có ý nghĩa cho màn chi tiết chiến dịch → bỏ nút "+" cũ (dead).
    VolunteerTopBar(
        onBack = onBackClick,
        center = {
            Text(
                text = routeLabel,
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    )
}
