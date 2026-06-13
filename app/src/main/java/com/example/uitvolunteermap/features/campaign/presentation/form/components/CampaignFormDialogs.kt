package com.example.uitvolunteermap.features.campaign.presentation.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormContentBackground
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormErrorText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormSecondaryText

@Composable
internal fun DiscardChangesDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Hủy thay đổi?",
                fontWeight = FontWeight.Bold,
                color = FormPrimaryText
            )
        },
        text = {
            Text(
                text = "Bạn có thay đổi chưa được lưu. Nếu thoát, các thay đổi này sẽ bị mất.",
                color = FormSecondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Hủy thay đổi", color = FormErrorText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Tiếp tục chỉnh sửa", color = FormPrimaryText)
            }
        },
        containerColor = FormContentBackground
    )
}

@Composable
internal fun PreloadErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = FormPrimaryText, style = MaterialTheme.typography.bodyLarge)
    }
}
