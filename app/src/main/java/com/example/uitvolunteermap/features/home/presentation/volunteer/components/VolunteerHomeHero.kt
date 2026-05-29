package com.example.uitvolunteermap.features.home.presentation.volunteer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerStatUiModel
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.PillShape
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBorder
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenHighlight
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSurface
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextMuted
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextSecondary

@Composable
internal fun VolunteerHomeHero(
    appName: String,
    isGuest: Boolean,
    roleBadge: String = "KHÁCH",
    stats: List<VolunteerStatUiModel> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 18.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = appName.ifBlank { "UIT · Tình nguyện" },
                    color = ScreenTextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chào bạn.",
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = roleBadge,
                    color = ScreenPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .border(1.dp, ScreenBorder, PillShape)
                        .background(ScreenSurface, PillShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, ScreenBorder, CircleShape)
                        .background(ScreenSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⋯",
                        color = ScreenTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = editorialHeadline(
                fullText = "Tháng sáu.\nMùa của\nnhững chiến dịch.",
                highlight = "Mùa"
            ),
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp,
                lineHeight = 36.sp
            ),
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = buildSummaryText(stats),
            color = ScreenTextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun editorialHeadline(
    fullText: String,
    highlight: String
): AnnotatedString {
    return buildAnnotatedString {
        val index = fullText.indexOf(highlight)
        if (index >= 0) {
            append(fullText.substring(0, index))
            pushStyle(
                SpanStyle(
                    background = ScreenHighlight,
                    color = ScreenTextPrimary
                )
            )
            append(highlight)
            pop()
            append(fullText.substring(index + highlight.length))
        } else {
            append(fullText)
        }
    }
}

private fun buildSummaryText(stats: List<VolunteerStatUiModel>): String {
    if (stats.isEmpty()) return "Đang tải dữ liệu..."
    val parts = stats.map { "${it.value} ${it.label.lowercase()}" }
    return "Bạn có ${parts.joinToString(", ")} trong hệ thống."
}
