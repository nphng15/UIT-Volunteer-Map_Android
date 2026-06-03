package com.example.uitvolunteermap.features.attendance.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

/**
 * Token màu/khoảng cách cho màn quản lý điểm danh, ánh xạ từ design MagicPath
 * sang [VolunteerFlowPalette] để đồng bộ với phần còn lại của app.
 */
object AttendanceTokens {
    val ScreenBackground = VolunteerFlowPalette.Background
    val Surface = VolunteerFlowPalette.Surface
    val Border = VolunteerFlowPalette.Border
    val Divider = VolunteerFlowPalette.Divider

    val TextPrimary = VolunteerFlowPalette.TextPrimary
    val TextSecondary = VolunteerFlowPalette.TextSecondary
    val TextMuted = VolunteerFlowPalette.TextMuted

    val Accent = VolunteerFlowPalette.BrandAccent
    val AccentSurface = Color(0xFFFFF0ED)

    val Success = VolunteerFlowPalette.Success
    val SuccessSurface = VolunteerFlowPalette.SuccessSurface
    val Danger = VolunteerFlowPalette.Danger
    val DangerSurface = VolunteerFlowPalette.DangerSurface

    val Warning = Color(0xFFB45309)
    val WarningSurface = Color(0xFFFFFBEB)

    val CardRadius = 20.dp
    val ScreenPadding = 16.dp
    val CardPadding = 14.dp
}
