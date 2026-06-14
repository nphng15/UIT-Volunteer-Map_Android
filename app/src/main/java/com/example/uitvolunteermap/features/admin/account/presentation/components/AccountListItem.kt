package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.account.presentation.AccountListItemUiModel

@Composable
internal fun AccountListItem(
    account: AccountListItemUiModel,
    showActions: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, AccountTokens.Border, RoundedCornerShape(24.dp))
            .background(AccountTokens.Surface, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar chữ cái đầu
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccountTokens.Brand.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = account.username.take(1).uppercase(),
                    color = AccountTokens.Brand,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.username,
                    color = AccountTokens.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = "Tham gia: ${account.joinedAt}",
                    color = AccountTokens.MutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            RoleBadge(role = account.role, label = account.roleName)
        }

        if (showActions) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    label = "Chỉnh sửa",
                    onClick = onEditClick,
                    isDanger = false,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    label = "Xóa",
                    onClick = onDeleteClick,
                    isDanger = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RoleBadge(
    role: com.example.uitvolunteermap.features.admin.account.presentation.AccountRole?,
    label: String
) {
    val (bg, fg) = roleBadgeColors(role)
    Text(
        text = (role?.label ?: label),
        color = fg,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun ActionButton(
    label: String,
    onClick: () -> Unit,
    isDanger: Boolean,
    modifier: Modifier = Modifier
) {
    val container = if (isDanger) AccountTokens.DangerSurface else AccountTokens.FieldBackground
    val content = if (isDanger) AccountTokens.Danger else AccountTokens.SecondaryText
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isDanger) Icons.Outlined.Delete else Icons.Outlined.Edit,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            color = content,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
