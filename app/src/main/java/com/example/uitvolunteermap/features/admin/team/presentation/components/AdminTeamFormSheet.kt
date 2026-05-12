package com.example.uitvolunteermap.features.admin.team.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamFormMode
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamFormState

/**
 * Sheet tạo / sửa đội.
 *
 * Create: hiện đủ teamName, leaderId, campaignId, description, imageUrl.
 * Edit: CHỈ hiện teamName, description, imageUrl — API không cho đổi leader/campaign nên
 * hai ô đó được ẩn hoàn toàn.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminTeamFormSheet(
    form: AdminTeamFormState,
    onTeamNameChange: (String) -> Unit,
    onLeaderIdChange: (String) -> Unit,
    onCampaignIdChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isCreate = form.mode == AdminTeamFormMode.Create

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AdminTeamTokens.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = if (isCreate) "Tạo đội mới" else "Chỉnh sửa đội",
                color = AdminTeamTokens.PrimaryText,
                fontWeight = FontWeight.ExtraBold,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isCreate) "Thiết lập thông tin cho đội hình mới"
                else "Cập nhật thông tin đội nhóm",
                color = AdminTeamTokens.SecondaryText,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(20.dp))

            AdminTeamField(
                value = form.teamName,
                onValueChange = onTeamNameChange,
                label = "Tên đội",
                placeholder = "VD: Đội Hậu Cần",
                enabled = !form.isSubmitting
            )

            if (isCreate) {
                Spacer(modifier = Modifier.height(14.dp))
                AdminTeamField(
                    value = form.leaderId,
                    onValueChange = onLeaderIdChange,
                    // TODO: thay bằng picker chọn nhóm trưởng khi có API danh sách user theo role.
                    label = "ID nhóm trưởng",
                    placeholder = "Nhập ID người dùng (số > 0)",
                    enabled = !form.isSubmitting,
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(14.dp))
                AdminTeamField(
                    value = form.campaignId,
                    onValueChange = onCampaignIdChange,
                    // TODO: thay bằng picker chọn chiến dịch (GET /campaigns) khi tích hợp.
                    label = "ID chiến dịch",
                    placeholder = "Nhập ID chiến dịch (số > 0)",
                    enabled = !form.isSubmitting,
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            AdminTeamField(
                value = form.description,
                onValueChange = onDescriptionChange,
                label = "Mô tả nhiệm vụ",
                placeholder = "Mô tả nhiệm vụ của đội...",
                enabled = !form.isSubmitting,
                singleLine = false,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))
            AdminTeamField(
                value = form.imageUrl,
                onValueChange = onImageUrlChange,
                label = "Ảnh đại diện (URL)",
                placeholder = "https://...",
                enabled = !form.isSubmitting,
                keyboardType = KeyboardType.Uri
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSubmit,
                enabled = !form.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminTeamTokens.Brand,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isCreate) "Tạo đội" else "Lưu thay đổi",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminTeamField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    enabled: Boolean,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = AdminTeamTokens.PrimaryText,
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            placeholder = { Text(text = placeholder, color = AdminTeamTokens.MutedText) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AdminTeamTokens.Brand,
                unfocusedBorderColor = AdminTeamTokens.Border,
                focusedTextColor = AdminTeamTokens.PrimaryText,
                unfocusedTextColor = AdminTeamTokens.PrimaryText,
                cursorColor = AdminTeamTokens.Brand
            )
        )
    }
}
