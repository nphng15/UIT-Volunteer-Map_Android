package com.example.uitvolunteermap.features.admin.team.presentation.components

import androidx.compose.ui.graphics.Color
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

/**
 * Token màu cho màn quản lý đội. Tái dùng [VolunteerFlowPalette] (yêu cầu: không tự định nghĩa
 * palette riêng) — đây chỉ là alias để code màn này đọc gọn, không tạo màu mới.
 */
internal object AdminTeamTokens {
    val ScreenBackgroundTop = VolunteerFlowPalette.AdminBackgroundTop
    val ScreenBackground = VolunteerFlowPalette.AdminBackgroundBottom
    val Surface = VolunteerFlowPalette.Surface
    val Border = VolunteerFlowPalette.Border
    val PrimaryText = VolunteerFlowPalette.TextPrimary
    val SecondaryText = VolunteerFlowPalette.TextSecondary
    val MutedText = VolunteerFlowPalette.TextMuted
    val Brand = VolunteerFlowPalette.BrandPrimary
    val Accent = VolunteerFlowPalette.BrandAccent
    val Danger = VolunteerFlowPalette.Danger
    val DangerSurface = VolunteerFlowPalette.DangerSurface
    val SurfaceVariant = VolunteerFlowPalette.SurfaceVariant
}
