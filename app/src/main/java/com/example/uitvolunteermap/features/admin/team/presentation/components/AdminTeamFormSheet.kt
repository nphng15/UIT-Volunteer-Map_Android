package com.example.uitvolunteermap.features.admin.team.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamCampaignOption
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamFormMode
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamFormState
import com.example.uitvolunteermap.features.admin.team.presentation.AdminTeamLeaderOption

/**
 * Sheet tạo / sửa đội.
 *
 * Create: chọn nhóm trưởng và chiến dịch theo tên; ID chỉ được giữ trong state để submit API.
 * Edit: CHỈ hiện teamName, description, imageUrl — API không cho đổi leader/campaign nên
 * hai selector đó được ẩn hoàn toàn.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminTeamFormSheet(
    form: AdminTeamFormState,
    leaderOptions: List<AdminTeamLeaderOption>,
    campaignOptions: List<AdminTeamCampaignOption>,
    isLoadingOptions: Boolean,
    onTeamNameChange: (String) -> Unit,
    onLeaderSelected: (Int) -> Unit,
    onCampaignSelected: (Int) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isCreate = form.mode == AdminTeamFormMode.Create
    val canSubmit = !form.isSubmitting && (!isCreate ||
        (form.selectedLeaderId != null && form.selectedCampaignId != null))

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
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isCreate) "Thiết lập thông tin cho đội hình mới"
                else "Cập nhật thông tin đội nhóm",
                color = AdminTeamTokens.SecondaryText,
                style = MaterialTheme.typography.bodySmall
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
                AdminTeamPickerField(
                    label = "Nhóm trưởng",
                    selectedLabel = leaderOptions.firstOrNull { it.userId == form.selectedLeaderId }?.displayName,
                    placeholder = if (isLoadingOptions) "Đang tải nhóm trưởng..." else "Chọn nhóm trưởng",
                    enabled = !form.isSubmitting && !isLoadingOptions && leaderOptions.isNotEmpty()
                ) {
                    leaderOptions.forEach { leader ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(leader.displayName, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        leader.subtitle,
                                        color = AdminTeamTokens.SecondaryText,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = { onLeaderSelected(leader.userId) }
                        )
                    }
                }
                if (!isLoadingOptions && leaderOptions.isEmpty()) {
                    PickerHint("Chưa có tài khoản leader để chọn.")
                }

                Spacer(modifier = Modifier.height(14.dp))
                AdminTeamPickerField(
                    label = "Chiến dịch",
                    selectedLabel = campaignOptions.firstOrNull { it.campaignId == form.selectedCampaignId }?.name,
                    placeholder = if (isLoadingOptions) "Đang tải chiến dịch..." else "Chọn chiến dịch",
                    enabled = !form.isSubmitting && !isLoadingOptions && campaignOptions.isNotEmpty()
                ) {
                    campaignOptions.forEach { campaign ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(campaign.name, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        campaign.dateRange,
                                        color = AdminTeamTokens.SecondaryText,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = { onCampaignSelected(campaign.campaignId) }
                        )
                    }
                }
                if (!isLoadingOptions && campaignOptions.isEmpty()) {
                    PickerHint("Chưa có chiến dịch để chọn.")
                }
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
                enabled = canSubmit,
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
private fun AdminTeamPickerField(
    label: String,
    selectedLabel: String?,
    placeholder: String,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = AdminTeamTokens.PrimaryText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLabel.orEmpty(),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = enabled,
                readOnly = true,
                singleLine = true,
                placeholder = { Text(text = placeholder, color = AdminTeamTokens.MutedText) },
                trailingIcon = {
                    Row {
                        if (!enabled && placeholder.startsWith("Đang tải")) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Rounded.ArrowDropDown, contentDescription = null)
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AdminTeamTokens.Brand,
                    unfocusedBorderColor = AdminTeamTokens.Border,
                    disabledBorderColor = AdminTeamTokens.Border,
                    focusedTextColor = AdminTeamTokens.PrimaryText,
                    unfocusedTextColor = AdminTeamTokens.PrimaryText,
                    disabledTextColor = AdminTeamTokens.PrimaryText,
                    cursorColor = AdminTeamTokens.Brand
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                content()
            }
        }
    }
}

@Composable
private fun PickerHint(text: String) {
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = text,
        color = AdminTeamTokens.SecondaryText,
        style = MaterialTheme.typography.bodySmall
    )
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
            style = MaterialTheme.typography.labelLarge,
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
