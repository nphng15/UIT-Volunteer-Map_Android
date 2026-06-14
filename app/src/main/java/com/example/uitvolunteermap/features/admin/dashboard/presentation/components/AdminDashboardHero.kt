package com.example.uitvolunteermap.features.admin.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

/**
 * Thẻ chào mừng trên cùng dashboard. Dùng gradient brand làm điểm nhấn, hiển thị tên
 * quản trị viên lấy từ phiên đăng nhập.
 */
@Composable
internal fun AdminDashboardHero(
    username: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        VolunteerFlowPalette.TextPrimary,
                        VolunteerFlowPalette.BrandPrimary
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = AdminDashboardTokens.TextInverse.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AdminPanelSettings,
                    contentDescription = null,
                    tint = AdminDashboardTokens.TextInverse
                )
            }
            Text(
                text = "Xin chào, $username!",
                color = AdminDashboardTokens.TextInverse,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Chào mừng bạn quay lại bảng điều khiển quản trị.",
                color = AdminDashboardTokens.TextInverse.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
