package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun ConfirmDeleteAccountDialog(
    username: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa tài khoản?",
                fontWeight = FontWeight.Bold,
                color = AccountTokens.PrimaryText
            )
        },
        text = {
            Text(
                text = buildString {
                    append("Tài khoản ")
                    append(username ?: "này")
                    append(" sẽ bị vô hiệu hóa. Bạn có chắc muốn tiếp tục không?")
                },
                color = AccountTokens.SecondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Xóa", color = AccountTokens.Danger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hủy", color = AccountTokens.PrimaryText)
            }
        },
        containerColor = AccountTokens.Surface
    )
}
