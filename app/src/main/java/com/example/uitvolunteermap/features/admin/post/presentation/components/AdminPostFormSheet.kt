package com.example.uitvolunteermap.features.admin.post.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette
import com.example.uitvolunteermap.features.admin.post.presentation.AdminPostFormMode
import com.example.uitvolunteermap.features.admin.post.presentation.AdminPostFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminPostFormSheet(
    form: AdminPostFormState,
    isSaving: Boolean,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onTeamIdChanged: (String) -> Unit,
    onAuthorIdChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VolunteerFlowPalette.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = if (form.mode == AdminPostFormMode.Create) {
                    "Đăng bài viết mới"
                } else {
                    "Chỉnh sửa bài viết"
                },
                color = VolunteerFlowPalette.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            FormTextField(
                value = form.title,
                onValueChange = onTitleChanged,
                label = "Tiêu đề",
                placeholder = "VD: Tổng kết chiến dịch Mùa Hè Xanh",
                singleLine = true
            )

            FormTextField(
                value = form.content,
                onValueChange = onContentChanged,
                label = "Nội dung",
                placeholder = "Chia sẻ khoảnh khắc, thông báo, cập nhật...",
                singleLine = false,
                minHeight = 120.dp
            )

            // TODO: thay 2 ô số bên dưới bằng team picker / author picker khi có API
            //       danh sách đội & người dùng. authorId là userId của tác giả (KHÔNG phải accId).
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    FormTextField(
                        value = form.teamIdInput,
                        onValueChange = onTeamIdChanged,
                        label = "Mã đội (teamId)",
                        placeholder = "VD: 1",
                        singleLine = true,
                        numeric = true
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormTextField(
                        value = form.authorIdInput,
                        onValueChange = onAuthorIdChanged,
                        label = "Mã tác giả (userId)",
                        placeholder = "VD: 5",
                        singleLine = true,
                        numeric = true
                    )
                }
            }

            Button(
                onClick = onSubmit,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VolunteerFlowPalette.BrandAccent,
                    contentColor = VolunteerFlowPalette.TextInverse
                )
            ) {
                Text(
                    text = when {
                        isSaving -> "Đang lưu..."
                        form.mode == AdminPostFormMode.Create -> "Đăng ngay"
                        else -> "Lưu thay đổi"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean,
    numeric: Boolean = false,
    minHeight: androidx.compose.ui.unit.Dp = 56.dp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        keyboardOptions = if (numeric) {
            KeyboardOptions(keyboardType = KeyboardType.Number)
        } else {
            KeyboardOptions.Default
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VolunteerFlowPalette.BrandAccent,
            unfocusedBorderColor = VolunteerFlowPalette.Border,
            focusedLabelColor = VolunteerFlowPalette.BrandAccent,
            cursorColor = VolunteerFlowPalette.BrandAccent,
            focusedTextColor = VolunteerFlowPalette.TextPrimary,
            unfocusedTextColor = VolunteerFlowPalette.TextPrimary
        )
    )
}
