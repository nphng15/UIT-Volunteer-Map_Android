package com.example.uitvolunteermap.features.attendance.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.attendance.presentation.MemberAttendanceUiModel
import kotlin.math.absoluteValue

@Composable
fun MemberAttendanceCard(
    member: MemberAttendanceUiModel,
    onPhotoTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AttendanceTokens.CardRadius),
        color = AttendanceTokens.Surface,
        shadowElevation = 1.dp,
        border = if (member.isSuspicious) {
            androidx.compose.foundation.BorderStroke(1.dp, AttendanceTokens.Warning.copy(alpha = 0.3f))
        } else {
            null
        }
    ) {
        Column(modifier = Modifier.clickable { onPhotoTap(member.userId) }) {
            Row(
                modifier = Modifier.padding(AttendanceTokens.CardPadding),
                verticalAlignment = Alignment.Top
            ) {
                Avatar(member.initials)
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.fullName,
                        color = AttendanceTokens.TextPrimary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    StatusBadge(member.hasCheckedIn)

                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconLabel(Icons.Outlined.Badge, member.mssv)
                        if (member.hasCheckedIn && member.checkedInAt != null) {
                            IconLabel(Icons.Outlined.Schedule, member.checkedInAt)
                        }
                    }

                    if (member.isTooFar) {
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = AttendanceTokens.Warning,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Cách ${member.distanceMeters!!.toKmLabel()}",
                                color = AttendanceTokens.Warning,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))
                PhotoThumbnail(
                    enabled = member.hasCheckedIn,
                    onClick = { onPhotoTap(member.userId) }
                )
            }

            if (member.isSuspicious) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AttendanceTokens.WarningSurface)
                        .padding(horizontal = AttendanceTokens.CardPadding, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = AttendanceTokens.Warning,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Khoảng cách bất thường: ${member.distanceMeters!!.toMeterLabel()} — có thể điểm danh hộ",
                        color = AttendanceTokens.Warning,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun Avatar(initials: String) {
    val palette = listOf(
        Color(0xFFFF5A3C), Color(0xFF2563FF), Color(0xFF8B5CF6),
        Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF6366F1)
    )
    val color = palette[initials.hashCode().absoluteValue % palette.size]
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(50))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusBadge(checkedIn: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (checkedIn) AttendanceTokens.SuccessSurface else AttendanceTokens.DangerSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (checkedIn) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = AttendanceTokens.Success,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(Modifier.width(3.dp))
            }
            Text(
                text = if (checkedIn) "Đã điểm danh" else "Chưa điểm danh",
                color = if (checkedIn) AttendanceTokens.Success else AttendanceTokens.Danger,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun IconLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AttendanceTokens.TextSecondary,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            color = AttendanceTokens.TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun PhotoThumbnail(enabled: Boolean, onClick: () -> Unit) {
    if (enabled) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AttendanceTokens.Accent)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Xem ảnh điểm danh",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(18.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, AttendanceTokens.Border, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                tint = AttendanceTokens.TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun Double.toMeterLabel(): String = "${this.toInt()}m"
private fun Double.toKmLabel(): String = "${"%.1f".format(this / 1000)}km"
