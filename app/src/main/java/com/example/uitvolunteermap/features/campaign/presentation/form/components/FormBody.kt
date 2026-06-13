package com.example.uitvolunteermap.features.campaign.presentation.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.form.CampaignFormMode
import com.example.uitvolunteermap.features.campaign.presentation.form.CampaignFormUiEvent
import com.example.uitvolunteermap.features.campaign.presentation.form.CampaignFormUiState
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormAccentSoft
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormBorder
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormErrorText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormSecondaryText

@Composable
internal fun FormBody(
    state: CampaignFormUiState,
    onEvent: (CampaignFormUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormField(
            label = "Tên chiến dịch *",
            value = state.campaignName,
            onValueChange = { onEvent(CampaignFormUiEvent.NameChanged(it)) },
            placeholder = "Nhập tên chiến dịch",
            singleLine = true
        )

        FormField(
            label = "Mô tả",
            value = state.description,
            onValueChange = { onEvent(CampaignFormUiEvent.DescriptionChanged(it)) },
            placeholder = "Nhập mô tả (tùy chọn)",
            singleLine = false,
            minLines = 3
        )

        DatePickerField(
            label = "Ngày bắt đầu *",
            value = state.startDate,
            placeholder = "yyyy-MM-dd",
            onDateSelected = { onEvent(CampaignFormUiEvent.StartDateChanged(it)) }
        )

        DatePickerField(
            label = "Ngày kết thúc *",
            value = state.endDate,
            placeholder = "yyyy-MM-dd",
            onDateSelected = { onEvent(CampaignFormUiEvent.EndDateChanged(it)) }
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = FormErrorText,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onEvent(CampaignFormUiEvent.SaveClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting && !state.isLoadingPreload,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FormAccentSoft,
                contentColor = FormPrimaryText,
                disabledContainerColor = FormBorder,
                disabledContentColor = FormSecondaryText
            )
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(18.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp,
                    color = FormPrimaryText
                )
            }
            Text(
                text = when (state.mode) {
                    CampaignFormMode.Create -> "Tạo chiến dịch"
                    CampaignFormMode.Edit -> "Lưu thay đổi"
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}
