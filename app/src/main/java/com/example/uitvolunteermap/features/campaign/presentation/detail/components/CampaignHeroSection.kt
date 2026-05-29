package com.example.uitvolunteermap.features.campaign.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenHighlight
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextMuted
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextSecondary

@Composable
internal fun CampaignHeroSection(
    routeSubtitle: String,
    title: String,
    supportingText: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = routeSubtitle.ifBlank { "01.06 - 30.08.26 · 90 NGÀY" },
            color = ScreenTextMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = editorialTitle(title),
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp,
                lineHeight = 36.sp
            ),
            fontWeight = FontWeight.ExtraBold
        )
        if (supportingText.isNotBlank()) {
            Text(
                text = supportingText,
                color = ScreenTextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun editorialTitle(text: String): AnnotatedString {
    val yearMatch = Regex("""\b\d{4}\b""").find(text)
        ?: return AnnotatedString(text)

    return buildAnnotatedString {
        append(text.substring(0, yearMatch.range.first))
        pushStyle(
            SpanStyle(
                background = ScreenHighlight,
                color = ScreenTextPrimary
            )
        )
        append(yearMatch.value)
        pop()
        append(text.substring(yearMatch.range.last + 1))
    }
}
