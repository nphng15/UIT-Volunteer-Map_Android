package com.example.uitvolunteermap.features.attendance.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.uitvolunteermap.features.attendance.presentation.MemberAttendanceUiModel

@Composable
fun PhotoProofDialog(
    member: MemberAttendanceUiModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = AttendanceTokens.Surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (member.hasCheckedIn) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AttendanceTokens.Accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (member.imageUrl != null) {
                            AsyncImage(
                                model = member.imageUrl,
                                contentDescription = "Ảnh điểm danh của ${member.fullName}",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = null,
                                tint = AttendanceTokens.Accent.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = member.fullName,
                    color = AttendanceTokens.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Badge,
                        contentDescription = null,
                        tint = AttendanceTokens.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = member.mssv,
                        color = AttendanceTokens.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
                if (member.hasCheckedIn) {
                    InfoRow("Thời gian", member.checkedInAt ?: "—")
                    if (member.distanceMeters != null) {
                        Spacer(Modifier.height(8.dp))
                        DistanceRow(member.distanceMeters)
                    }
                } else {
                    InfoStatusRow()
                }

                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AttendanceTokens.Accent)
                        .clickable { onDismiss() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Đóng",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoStatusRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Trạng thái", color = AttendanceTokens.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Surface(
            shape = RoundedCornerShape(50),
            color = AttendanceTokens.DangerSurface
        ) {
            Text(
                text = "Chưa điểm danh",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = AttendanceTokens.Danger,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = AttendanceTokens.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            color = AttendanceTokens.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DistanceRow(distanceMeters: Double) {
    val suspicious = distanceMeters > 300
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Khoảng cách", color = AttendanceTokens.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Surface(
            shape = RoundedCornerShape(50),
            color = if (suspicious) AttendanceTokens.WarningSurface else AttendanceTokens.SuccessSurface
        ) {
            Text(
                text = "${distanceMeters.toInt()}m",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = if (suspicious) AttendanceTokens.Warning else AttendanceTokens.Success,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
