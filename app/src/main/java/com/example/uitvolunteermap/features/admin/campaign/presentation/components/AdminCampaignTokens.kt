package com.example.uitvolunteermap.features.admin.campaign.presentation.components

import androidx.compose.ui.graphics.Color
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

/**
 * Token màu cho khu quản trị chiến dịch. Tái sử dụng VolunteerFlowPalette
 * (không định nghĩa palette riêng) để giữ tính nhất quán toàn app.
 */
internal object AdminCampaignTokens {
    val TopBackground = VolunteerFlowPalette.AdminBackgroundTop
    val ContentBackground = VolunteerFlowPalette.AdminBackgroundBottom
    val Surface = VolunteerFlowPalette.Surface
    val Border = VolunteerFlowPalette.Border
    val PrimaryText = VolunteerFlowPalette.TextPrimary
    val SecondaryText = VolunteerFlowPalette.TextSecondary
    val MutedText = VolunteerFlowPalette.TextMuted
    val Accent = VolunteerFlowPalette.BrandAccent
    val AccentPressed = VolunteerFlowPalette.BrandAccentPressed
    val Brand = VolunteerFlowPalette.BrandPrimary
    val Danger = VolunteerFlowPalette.Danger
    val DangerSurface = VolunteerFlowPalette.DangerSurface
    val SurfaceVariant = VolunteerFlowPalette.SurfaceVariant
    val FieldBackground = VolunteerFlowPalette.SurfaceVariant
    val Inverse: Color = VolunteerFlowPalette.TextInverse
}
