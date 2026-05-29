package com.example.uitvolunteermap.features.campaign.presentation.team.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamHighlight
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamMuted
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimary
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSecondary

@Composable
internal fun TeamTitleSection(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "MHX 2026",
            color = TeamMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = highlightedTeamTitle(title),
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = description,
            color = TeamSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

private fun highlightedTeamTitle(title: String) = buildAnnotatedString {
    val words = title.split(Regex("\\s+")).filter { it.isNotBlank() }
    if (words.isEmpty()) return@buildAnnotatedString

    append(words.first())
    if (words.size > 1) {
        append(" ")
        withStyle(SpanStyle(background = TeamHighlight)) {
            append(words.drop(1).joinToString(" "))
        }
    }
    append(".")
}
