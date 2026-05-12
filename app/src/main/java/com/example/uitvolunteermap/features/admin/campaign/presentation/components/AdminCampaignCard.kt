package com.example.uitvolunteermap.features.admin.campaign.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.campaign.presentation.AdminCampaignItemUiModel

@Composable
internal fun AdminCampaignCard(
    campaign: AdminCampaignItemUiModel,
    showActions: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(width = 1.dp, color = AdminCampaignTokens.Border, shape = RoundedCornerShape(24.dp))
            .background(color = AdminCampaignTokens.Surface, shape = RoundedCornerShape(24.dp))
            .clickable(enabled = showActions, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar khởi tạo từ chữ cái đầu (mô phỏng ManagementCard của design)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = AdminCampaignTokens.Brand.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = campaign.campaignName.trim().firstOrNull()?.uppercase() ?: "?",
                    color = AdminCampaignTokens.Brand,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = campaign.campaignName,
                    color = AdminCampaignTokens.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = campaign.dateRange,
                    color = AdminCampaignTokens.SecondaryText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (campaign.hasLocation) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Có vị trí điểm danh",
                    tint = AdminCampaignTokens.Brand,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (campaign.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = campaign.description,
                color = AdminCampaignTokens.SecondaryText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (campaign.checkInRadius != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bán kính điểm danh: ${formatNumber(campaign.checkInRadius)} m",
                color = AdminCampaignTokens.MutedText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }

        if (showActions) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Chỉnh sửa chiến dịch",
                        tint = AdminCampaignTokens.Brand,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Xóa chiến dịch",
                        tint = AdminCampaignTokens.Danger,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/** Bỏ phần ".0" thừa cho số tròn để hiển thị gọn (vd 100.0 → "100"). */
private fun formatNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
