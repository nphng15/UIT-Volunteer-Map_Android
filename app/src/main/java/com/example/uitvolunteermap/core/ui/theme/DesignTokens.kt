package com.example.volunteermap.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ActiveHexPalette {
    val Values: List<String> = listOf(
        "#D8E5EC",
        "#F85A00",
        "#FFE5D6",
        "#1F3673",
        "#F9FDFF",
        "#439A65",
        "#DDF3F8",
        "#FFFFFF",
        "#1C3977",
        "#6D839A",
    )
}

object ColorTokens {
    val BorderSubtle = Color(0xFFD8E5EC)
    val BrandPrimary = Color(0xFF1F3673)
    val BrandAccent = Color(0xFFF85A00)
    val BrandAccentSoft = Color(0xFFFFE5D6)
    val BrandSurface = Color(0xFFF9FDFF)

    val TextPrimary = Color(0xFF1C3977)
    val TextSecondary = Color(0xFF6D839A)
    val TextInverse = Color(0xFFFFFFFF)

    val ScreenBackgroundTop = Color(0xFFDDF3F8)
    val ScreenBackgroundBottom = Color(0xFFF9FDFF)

    val MapMarker = Color(0xFF439A65)
    val MapMarkerActive = Color(0xFFF85A00)
}

object DesignColorScheme {
    val Light = lightColorScheme(
        primary = ColorTokens.BrandPrimary,
        onPrimary = ColorTokens.TextInverse,
        primaryContainer = ColorTokens.ScreenBackgroundTop,
        onPrimaryContainer = ColorTokens.TextPrimary,
        secondary = ColorTokens.BrandAccent,
        onSecondary = ColorTokens.TextInverse,
        secondaryContainer = ColorTokens.BrandAccentSoft,
        onSecondaryContainer = ColorTokens.TextPrimary,
        tertiary = ColorTokens.MapMarker,
        onTertiary = ColorTokens.TextInverse,
        tertiaryContainer = ColorTokens.ScreenBackgroundTop,
        onTertiaryContainer = ColorTokens.TextPrimary,
        background = ColorTokens.BrandSurface,
        onBackground = ColorTokens.TextPrimary,
        surface = ColorTokens.TextInverse,
        onSurface = ColorTokens.TextPrimary,
        surfaceVariant = ColorTokens.ScreenBackgroundTop,
        onSurfaceVariant = ColorTokens.TextSecondary,
        outline = ColorTokens.BorderSubtle,
        error = ColorTokens.BrandAccent,
        onError = ColorTokens.TextInverse,
    )
    val Dark = Light
}

object FontTokens {
    const val HeadingName = "Bricolage Grotesque"
    const val BodyName = "DM Sans"
    const val UtilityName = "Inter"
    const val FormName = "Outfit"

    val Heading = FontFamily.SansSerif
    val Body = FontFamily.SansSerif
    val Utility = FontFamily.SansSerif
    val Form = FontFamily.SansSerif
}

object TextSize {
    val Size9 = 9.sp
    val Size10 = 10.sp
    val Size11 = 11.sp
    val Size12 = 12.sp
    val Size13 = 13.sp
    val Size14 = 14.sp
    val Size15 = 15.sp
    val Size16 = 16.sp
    val Size18 = 18.sp
    val Size20 = 20.sp
    val Size22 = 22.sp
    val Size24 = 24.sp
    val Size26 = 26.sp
    val Size28 = 28.sp
    val Size31 = 31.sp
    val Size32 = 32.sp
}

object LineHeight {
    val Tight = 1.05f
    val Compact = 1.35f
    val Normal = 1.4f
    val Relaxed = 1.45f
    val Loose = 1.5f
}

val DesignTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontTokens.Heading,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size32,
        lineHeight = (TextSize.Size32.value * LineHeight.Tight).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontTokens.Heading,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size28,
        lineHeight = (TextSize.Size28.value * LineHeight.Tight).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontTokens.Heading,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size22,
    ),
    titleLarge = TextStyle(
        fontFamily = FontTokens.Heading,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size20,
    ),
    titleMedium = TextStyle(
        fontFamily = FontTokens.Body,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size15,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontTokens.Body,
        fontWeight = FontWeight.Medium,
        fontSize = TextSize.Size14,
        lineHeight = (TextSize.Size14.value * LineHeight.Normal).sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontTokens.Body,
        fontWeight = FontWeight.Medium,
        fontSize = TextSize.Size13,
        lineHeight = (TextSize.Size13.value * LineHeight.Relaxed).sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontTokens.Utility,
        fontWeight = FontWeight.SemiBold,
        fontSize = TextSize.Size12,
    ),
    labelLarge = TextStyle(
        fontFamily = FontTokens.Body,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size13,
    ),
    labelMedium = TextStyle(
        fontFamily = FontTokens.Body,
        fontWeight = FontWeight.SemiBold,
        fontSize = TextSize.Size11,
    ),
    labelSmall = TextStyle(
        fontFamily = FontTokens.Utility,
        fontWeight = FontWeight.Bold,
        fontSize = TextSize.Size10,
    ),
)

object Dimens {
    val ScreenWidth = 390.dp
    val StatusBarHeight = 62.dp
    val TopBarHeight = 72.dp

    // Spacing scale
    val Spacing0 = 0.dp
    val Spacing2 = 2.dp
    val Spacing4 = 4.dp
    val Spacing6 = 6.dp
    val Spacing8 = 8.dp
    val Spacing10 = 10.dp
    val Spacing12 = 12.dp
    val Spacing14 = 14.dp
    val Spacing16 = 16.dp
    val Spacing20 = 20.dp
    val Spacing24 = 24.dp
    val Spacing28 = 28.dp
    val Spacing32 = 32.dp

    // Common component sizes
    val IconSmall = 18.dp
    val IconMedium = 22.dp
    val Avatar = 88.dp
    val CampaignCoverHeight = 116.dp
    val HeroCardHeight = 196.dp
    val BottomSheetPeek = 240.dp
}

object Shapes {
    val Radius10 = 10.dp
    val Radius12 = 12.dp
    val Radius16 = 16.dp
    val Radius18 = 18.dp
    val Radius22 = 22.dp
    val Radius24 = 24.dp
    val Radius28 = 28.dp
    val Radius30 = 30.dp
    val Radius44 = 44.dp
    val RadiusPill = 100.dp
    val RadiusCircle = 999.dp
}

object Elevations {
    val Level0 = 0.dp
    val Level4 = 4.dp
    val Level6 = 6.dp
    val Level8 = 8.dp
    val Level10 = 10.dp
    val Level12 = 12.dp
}
