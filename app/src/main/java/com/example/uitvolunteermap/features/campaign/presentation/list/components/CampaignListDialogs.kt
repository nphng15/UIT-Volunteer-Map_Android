package com.example.uitvolunteermap.features.campaign.presentation.list.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListContentBackground
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListDeleteIconTint
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListSecondaryText

@Composable
internal fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa chiến dịch?",
                fontWeight = FontWeight.Bold,
                color = ListPrimaryText
            )
        },
        text = {
            Text(
                text = "Hành động này không thể hoàn tác. Bạn có chắc muốn xóa chiến dịch này không?",
                color = ListSecondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Xóa",
                    color = ListDeleteIconTint,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hủy", color = ListPrimaryText)
            }
        },
        containerColor = ListContentBackground
    )
}
