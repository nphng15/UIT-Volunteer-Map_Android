package com.example.uitvolunteermap.features.admin.team.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun ConfirmDeleteTeamDialog(
    teamName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa đội nhóm?",
                fontWeight = FontWeight.Bold,
                color = AdminTeamTokens.PrimaryText
            )
        },
        text = {
            Text(
                text = "\"$teamName\" và danh sách thành viên sẽ bị giải tán. " +
                    "Hành động này không thể hoàn tác.",
                color = AdminTeamTokens.SecondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Xóa", color = AdminTeamTokens.Danger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hủy", color = AdminTeamTokens.PrimaryText)
            }
        },
        containerColor = AdminTeamTokens.Surface
    )
}
