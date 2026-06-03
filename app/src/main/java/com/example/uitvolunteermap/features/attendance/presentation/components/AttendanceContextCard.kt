package com.example.uitvolunteermap.features.attendance.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.attendance.presentation.AttendanceUiState

@Composable
fun AttendanceContextCard(
    state: AttendanceUiState,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 800),
        label = "attendance_progress"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AttendanceTokens.CardRadius),
        color = AttendanceTokens.Surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AttendanceTokens.Accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = AttendanceTokens.Accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = state.teamName,
                    color = AttendanceTokens.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = AttendanceTokens.TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = state.date,
                    color = AttendanceTokens.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                StatColumn(state.checkedInCount.toString(), "Đã điểm danh", AttendanceTokens.Accent)
                Divider()
                StatColumn(state.notCheckedInCount.toString(), "Chưa điểm danh", AttendanceTokens.Danger)
                Divider()
                StatColumn(state.totalCount.toString(), "Tổng thành viên", AttendanceTokens.TextPrimary)
            }

            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AttendanceTokens.Divider)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AttendanceTokens.Accent)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tiến độ: ${(state.progress * 100).toInt()}%",
                modifier = Modifier.fillMaxWidth(),
                color = AttendanceTokens.Accent,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = AttendanceTokens.TextSecondary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(AttendanceTokens.Divider)
    )
}
