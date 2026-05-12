package com.example.uitvolunteermap.features.admin.campaign.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun AdminCampaignEmptyState(
    hasSearchQuery: Boolean,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = AdminCampaignTokens.SurfaceVariant, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = AdminCampaignTokens.MutedText,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasSearchQuery) "Không tìm thấy chiến dịch nào" else "Chưa có chiến dịch nào",
            color = AdminCampaignTokens.PrimaryText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (!hasSearchQuery) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nhấn nút + để tạo chiến dịch đầu tiên.",
                color = AdminCampaignTokens.SecondaryText,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRefresh,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminCampaignTokens.SurfaceVariant,
                    contentColor = AdminCampaignTokens.PrimaryText
                )
            ) {
                Text(text = "Làm mới", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun AdminCampaignErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = AdminCampaignTokens.PrimaryText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AdminCampaignTokens.Accent,
                contentColor = AdminCampaignTokens.Inverse
            )
        ) {
            Text(text = "Thử lại", fontWeight = FontWeight.Bold)
        }
    }
}
