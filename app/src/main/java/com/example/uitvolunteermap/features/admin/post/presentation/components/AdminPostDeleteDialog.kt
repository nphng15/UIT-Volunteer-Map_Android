package com.example.uitvolunteermap.features.admin.post.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

@Composable
internal fun AdminPostDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa bài viết?",
                fontWeight = FontWeight.Bold,
                color = VolunteerFlowPalette.TextPrimary
            )
        },
        text = {
            Text(
                text = "Bài viết sẽ bị ẩn khỏi hệ thống. Bạn có chắc muốn xóa không?",
                color = VolunteerFlowPalette.TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Xóa",
                    color = VolunteerFlowPalette.Danger,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hủy", color = VolunteerFlowPalette.TextPrimary)
            }
        },
        containerColor = VolunteerFlowPalette.Surface
    )
}
