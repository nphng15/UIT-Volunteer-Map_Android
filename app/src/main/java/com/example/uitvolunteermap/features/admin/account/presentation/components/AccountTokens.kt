package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.ui.graphics.Color
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette
import com.example.uitvolunteermap.features.admin.account.presentation.AccountRole

/**
 * Token màu cho khu Account Management. Tái sử dụng [VolunteerFlowPalette] thay vì
 * định nghĩa palette riêng, đúng quy ước của các màn admin.
 */
internal object AccountTokens {
    val BackgroundTop = VolunteerFlowPalette.AdminBackgroundTop
    val BackgroundBottom = VolunteerFlowPalette.AdminBackgroundBottom
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
    val FieldBackground = VolunteerFlowPalette.SurfaceVariant
}

/** Cặp màu (nền, chữ) cho badge vai trò; null role → màu trung tính. */
internal fun roleBadgeColors(role: AccountRole?): Pair<Color, Color> = when (role) {
    AccountRole.ADMIN -> VolunteerFlowPalette.DangerSurface to VolunteerFlowPalette.Danger
    AccountRole.LEADER -> VolunteerFlowPalette.BrandPrimary.copy(alpha = 0.12f) to VolunteerFlowPalette.BrandPrimary
    AccountRole.VOLUNTEER -> VolunteerFlowPalette.SuccessSurface to VolunteerFlowPalette.Success
    null -> VolunteerFlowPalette.Divider to VolunteerFlowPalette.TextSecondary
}
