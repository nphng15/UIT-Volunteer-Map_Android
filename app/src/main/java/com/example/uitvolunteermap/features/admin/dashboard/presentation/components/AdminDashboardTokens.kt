package com.example.uitvolunteermap.features.admin.dashboard.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

/**
 * Token cục bộ cho dashboard quản trị — ánh xạ trực tiếp sang [VolunteerFlowPalette]
 * để dùng chung bộ màu admin, không tạo palette riêng.
 */
internal object AdminDashboardTokens {
    val ScreenBackgroundTop = VolunteerFlowPalette.AdminBackgroundTop
    val ScreenBackgroundBottom = VolunteerFlowPalette.AdminBackgroundBottom

    val Surface = VolunteerFlowPalette.Surface
    val Border = VolunteerFlowPalette.Border

    val TextPrimary = VolunteerFlowPalette.TextPrimary
    val TextSecondary = VolunteerFlowPalette.TextSecondary
    val TextMuted = VolunteerFlowPalette.TextMuted
    val TextInverse = VolunteerFlowPalette.TextInverse

    val BrandPrimary = VolunteerFlowPalette.BrandPrimary
    val BrandAccent = VolunteerFlowPalette.BrandAccent
    val Success = VolunteerFlowPalette.Success
    val Info = VolunteerFlowPalette.Info

    val CardShape = RoundedCornerShape(24.dp)
}
