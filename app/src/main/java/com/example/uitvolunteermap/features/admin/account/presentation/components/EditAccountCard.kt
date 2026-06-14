package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.account.presentation.AccountRole
import com.example.uitvolunteermap.features.admin.account.presentation.EditAccountForm

/**
 * Form chỉnh sửa tài khoản. Backend chỉ cho phép đổi mật khẩu & vai trò, nên các
 * trường khác (username) chỉ hiển thị, không sửa được.
 */
@Composable
internal fun EditAccountCard(
    form: EditAccountForm,
    onClose: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (AccountRole) -> Unit,
    onSubmit: () -> Unit
) {
    SheetContainer {
        SheetHeader(
            title = "Chỉnh sửa tài khoản",
            subtitle = "Chỉ có thể đổi mật khẩu và vai trò.",
            enabled = !form.isSubmitting,
            onClose = onClose
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Username chỉ đọc
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccountTokens.FieldBackground)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Tên đăng nhập",
                    color = AccountTokens.MutedText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = form.username,
                    color = AccountTokens.PrimaryText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AccountFormField(
                label = "Mật khẩu mới (để trống nếu không đổi)",
                value = form.password,
                onValueChange = onPasswordChange,
                placeholder = "••••••",
                enabled = !form.isSubmitting,
                isPassword = true
            )

            RoleSelector(
                selected = form.role,
                onSelected = onRoleChange,
                enabled = !form.isSubmitting
            )

            if (form.errorMessage != null) {
                Text(
                    text = form.errorMessage,
                    color = AccountTokens.Danger,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        PrimaryActionButton(
            label = "Lưu thay đổi",
            isLoading = form.isSubmitting,
            onClick = onSubmit
        )
    }
}
