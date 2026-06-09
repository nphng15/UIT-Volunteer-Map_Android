package com.example.uitvolunteermap.features.admin.team.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamUiModel

@Composable
internal fun AdminTeamListItem(
    team: AdminTeamUiModel,
    showActions: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val checkInStatus = if (team.isCheckInConfigured) "Đã cấu hình check-in" else "Chưa cấu hình check-in"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(width = 1.dp, color = AdminTeamTokens.Border, shape = RoundedCornerShape(20.dp))
            .background(color = AdminTeamTokens.Surface, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar đội: ảnh nếu có, ngược lại chữ cái đầu trong khối bo góc.
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AdminTeamTokens.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!team.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = team.imageUrl,
                        contentDescription = team.teamName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Text(
                        text = team.teamName.firstOrNull()?.uppercase() ?: "?",
                        color = AdminTeamTokens.Brand,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.teamName,
                    color = AdminTeamTokens.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Trưởng đội: ${team.leadersLabel}",
                    color = AdminTeamTokens.SecondaryText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (team.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = team.description,
                color = AdminTeamTokens.SecondaryText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = checkInStatus,
            color = AdminTeamTokens.MutedText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Group,
                contentDescription = null,
                tint = AdminTeamTokens.MutedText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "${team.memberCount} thành viên",
                color = AdminTeamTokens.MutedText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )

            if (showActions) {
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Sửa đội",
                            tint = AdminTeamTokens.Brand,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Xóa đội",
                            tint = AdminTeamTokens.Danger,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
