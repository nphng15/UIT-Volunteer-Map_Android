package com.example.uitvolunteermap.features.home.presentation.volunteer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerStatUiModel
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBorder
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSurface
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextSecondary

@Composable
internal fun OverviewStatsStrip(stats: List<VolunteerStatUiModel>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, ScreenBorder, RoundedCornerShape(22.dp))
                    .background(ScreenSurface, RoundedCornerShape(22.dp))
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.value,
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stat.label,
                    color = ScreenTextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
