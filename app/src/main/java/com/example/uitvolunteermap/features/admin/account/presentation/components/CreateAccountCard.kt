package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.account.presentation.CreateAccountForm
import com.example.uitvolunteermap.features.admin.account.presentation.AccountRole

/** Nội dung form tạo tài khoản, đặt trong ModalBottomSheet. */
@Composable
internal fun CreateAccountCard(
    form: CreateAccountForm,
    onClose: () -> Unit,
    onFullnameChange: (String) -> Unit,
    onMssvChange: (String) -> Unit,
    onClassChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (AccountRole) -> Unit,
    onSubmit: () -> Unit
) {
    SheetContainer {
        SheetHeader(
            title = "Thêm tài khoản mới",
            subtitle = "Điền thông tin để tạo tài khoản tình nguyện viên.",
            enabled = !form.isSubmitting,
            onClose = onClose
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 460.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccountFormField(
                label = "Họ và tên",
                value = form.fullname,
                onValueChange = onFullnameChange,
                placeholder = "Nguyễn Văn A",
                enabled = !form.isSubmitting
            )
            AccountFormField(
                label = "MSSV (8 ký tự)",
                value = form.mssv,
                onValueChange = onMssvChange,
                placeholder = "22521000",
                enabled = !form.isSubmitting,
                keyboardType = KeyboardType.Number
            )
            AccountFormField(
                label = "Lớp",
                value = form.className,
                onValueChange = onClassChange,
                placeholder = "KTPM2022",
                enabled = !form.isSubmitting
            )
            AccountFormField(
                label = "Email (@gm.uit.edu.vn)",
                value = form.email,
                onValueChange = onEmailChange,
                placeholder = "abc@gm.uit.edu.vn",
                enabled = !form.isSubmitting,
                keyboardType = KeyboardType.Email
            )
            AccountFormField(
                label = "Số điện thoại",
                value = form.phoneNumber,
                onValueChange = onPhoneChange,
                placeholder = "0901234567",
                enabled = !form.isSubmitting,
                keyboardType = KeyboardType.Phone
            )
            AccountFormField(
                label = "Tên đăng nhập",
                value = form.username,
                onValueChange = onUsernameChange,
                placeholder = "nguyenvana",
                enabled = !form.isSubmitting
            )
            AccountFormField(
                label = "Mật khẩu (tối thiểu 6 ký tự)",
                value = form.password,
                onValueChange = onPasswordChange,
                placeholder = "••••••",
                enabled = !form.isSubmitting,
                isPassword = true
            )
            RoleSelector(
                selected = form.role,
                onSelected = onRoleChange,
                enabled = !form.isSubmitting,
                roles = listOf(AccountRole.VOLUNTEER, AccountRole.LEADER)
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
            label = "Tạo tài khoản",
            isLoading = form.isSubmitting,
            onClick = onSubmit
        )
    }
}
