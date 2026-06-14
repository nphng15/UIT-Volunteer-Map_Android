package com.example.uitvolunteermap.features.admin.campaign.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun AdminCampaignDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa chiến dịch?",
                fontWeight = FontWeight.Bold,
                color = AdminCampaignTokens.PrimaryText
            )
        },
        text = {
            Text(
                text = "Chiến dịch và toàn bộ dữ liệu liên quan sẽ bị xóa vĩnh viễn. Hành động này không thể hoàn tác.",
                color = AdminCampaignTokens.SecondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Xóa", color = AdminCampaignTokens.Danger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hủy", color = AdminCampaignTokens.PrimaryText)
            }
        },
        containerColor = AdminCampaignTokens.Surface
    )
}
