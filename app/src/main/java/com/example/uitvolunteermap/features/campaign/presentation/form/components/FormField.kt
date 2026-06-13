package com.example.uitvolunteermap.features.campaign.presentation.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormBorder
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormSecondaryText

@Composable
internal fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = FormPrimaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = placeholder, color = FormSecondaryText)
            },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FormPrimaryText,
                unfocusedBorderColor = FormBorder,
                focusedTextColor = FormPrimaryText,
                unfocusedTextColor = FormPrimaryText,
                cursorColor = FormPrimaryText
            )
        )
    }
}
