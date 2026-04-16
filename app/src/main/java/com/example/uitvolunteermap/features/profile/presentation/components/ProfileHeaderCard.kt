package com.example.uitvolunteermap.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.UserRole
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.Shapes

@Composable
fun ProfileHeaderCard(
    fullName: String,
    role: UserRole,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Shapes.Radius24), // Sử dụng Shapes Token
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Spacing24), // Sử dụng Dimens Token
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.Avatar)
                    .background(
                        brush = Brush.radialGradient(
                            // SỬA: Thay màu Hex bằng các màu từ Palette hoặc Theme nếu có thể
                            // Ở đây tạm dùng màu từ Brand hoặc Surface để đồng bộ
                            colors = listOf(ColorTokens.ScreenBackgroundTop, ColorTokens.MapMarker.copy(alpha = 0.5f)),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = deriveInitials(fullName),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontTokens.Utility, // Dùng FontTokens.Utility (Inter)
                        fontWeight = FontWeight.Bold,
                        color = ColorTokens.TextPrimary,
                    ),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
            ) {
                Text(
                    text = fullName.ifBlank { "NA" },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontTokens.Heading, // Dùng FontTokens.Heading (Bricolage)
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                val (label, bgColor, textColor) = getRoleUi(role)
                Surface(
                    shape = RoundedCornerShape(Shapes.RadiusPill), // Dùng Shapes Token cho Pill shape
                    color = bgColor,
                    border = BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontTokens.Heading, // Dùng FontTokens thống nhất
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

private fun getRoleUi(role: UserRole): Triple<String, Color, Color> {
    return when (role) {
        UserRole.LEADER -> Triple(
            "LEADER",
            ColorTokens.BrandAccentSoft,
            ColorTokens.BrandAccent, // Dùng BrandAccent cho text để nổi bật hơn
        )
        UserRole.ADMIN -> Triple(
            "ADMIN",
            ColorTokens.ScreenBackgroundTop, // Tận dụng các màu trong Palette
            ColorTokens.BrandPrimary,
        )
        UserRole.GUEST -> Triple(
            "GUEST",
            ColorTokens.BrandSurface,
            ColorTokens.TextSecondary,
        )
    }
}

private fun deriveInitials(fullName: String): String {
    val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "NA"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}