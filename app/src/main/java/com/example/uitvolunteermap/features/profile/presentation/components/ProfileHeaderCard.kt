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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.profile.domain.entity.UserRole

private val InterFont = FontFamily.SansSerif
private val BricolageGrotesque = FontFamily.SansSerif

@Composable
fun ProfileHeaderCard(
    fullName: String,
    role: UserRole,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.Avatar)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFC8F0D8), Color(0xFF8DC8A8)),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = deriveInitials(fullName),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D5C3A),
                    ),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
            ) {
                Text(
                    text = if (fullName.isBlank()) "NA" else fullName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = BricolageGrotesque,
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                val (label, bgColor, textColor) = getRoleUi(role)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = bgColor,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    border = BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = BricolageGrotesque,
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
        UserRole.ADMIN -> Triple(
            "LEADER",
            Color(0xFFEAF5EE),
            Color(0xFF1D5C3A),
        )
        UserRole.STUDENT -> Triple(
            "STUDENT",
            Color(0xFFF2F2F2),
            Color(0xFF6D6C6A),
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
