package com.example.uitvolunteermap.features.admin.campaign.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.campaign.presentation.AdminCampaignFormState
import com.example.uitvolunteermap.features.admin.campaign.presentation.AdminCampaignUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminCampaignFormSheet(
    form: AdminCampaignFormState,
    sheetState: SheetState,
    onEvent: (AdminCampaignUiEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(AdminCampaignUiEvent.FormDismissed) },
        sheetState = sheetState,
        containerColor = AdminCampaignTokens.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = if (form.isEditing) "Chỉnh sửa chiến dịch" else "Tạo chiến dịch mới",
                color = AdminCampaignTokens.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            FormTextField(
                label = "Tên chiến dịch",
                value = form.campaignName,
                placeholder = "VD: Mùa Hè Xanh 2026",
                onValueChange = { onEvent(AdminCampaignUiEvent.FormNameChanged(it)) }
            )

            FormTextField(
                label = "Mô tả",
                value = form.description,
                placeholder = "Mô tả ngắn về chiến dịch...",
                singleLine = false,
                minLines = 3,
                onValueChange = { onEvent(AdminCampaignUiEvent.FormDescriptionChanged(it)) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    label = "Bắt đầu (yyyy-MM-dd)",
                    value = form.startDate,
                    placeholder = "2026-06-15",
                    modifier = Modifier.weight(1f),
                    onValueChange = { onEvent(AdminCampaignUiEvent.FormStartDateChanged(it)) }
                )
                FormTextField(
                    label = "Kết thúc (yyyy-MM-dd)",
                    value = form.endDate,
                    placeholder = "2026-08-15",
                    modifier = Modifier.weight(1f),
                    onValueChange = { onEvent(AdminCampaignUiEvent.FormEndDateChanged(it)) }
                )
            }

            Text(
                text = "Vị trí điểm danh (tuỳ chọn)",
                color = AdminCampaignTokens.SecondaryText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    label = "Vĩ độ",
                    value = form.latitude,
                    placeholder = "10.8700",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f),
                    onValueChange = { onEvent(AdminCampaignUiEvent.FormLatitudeChanged(it)) }
                )
                FormTextField(
                    label = "Kinh độ",
                    value = form.longitude,
                    placeholder = "106.8030",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f),
                    onValueChange = { onEvent(AdminCampaignUiEvent.FormLongitudeChanged(it)) }
                )
            }

            FormTextField(
                label = "Bán kính điểm danh (m)",
                value = form.checkInRadius,
                placeholder = "100",
                keyboardType = KeyboardType.Decimal,
                onValueChange = { onEvent(AdminCampaignUiEvent.FormCheckInRadiusChanged(it)) }
            )

            if (form.errorMessage != null) {
                Text(
                    text = form.errorMessage,
                    color = AdminCampaignTokens.Danger,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = { onEvent(AdminCampaignUiEvent.FormSubmitted) },
                enabled = !form.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminCampaignTokens.Accent,
                    contentColor = AdminCampaignTokens.Inverse
                )
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = AdminCampaignTokens.Inverse,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (form.isEditing) "Lưu thay đổi" else "Tạo chiến dịch",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = AdminCampaignTokens.SecondaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = AdminCampaignTokens.MutedText) },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AdminCampaignTokens.FieldBackground,
                unfocusedContainerColor = AdminCampaignTokens.FieldBackground,
                focusedBorderColor = AdminCampaignTokens.Accent,
                unfocusedBorderColor = AdminCampaignTokens.Border,
                focusedTextColor = AdminCampaignTokens.PrimaryText,
                unfocusedTextColor = AdminCampaignTokens.PrimaryText
            )
        )
    }
}
