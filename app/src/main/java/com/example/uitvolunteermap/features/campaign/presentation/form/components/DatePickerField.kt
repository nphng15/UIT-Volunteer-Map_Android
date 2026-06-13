package com.example.uitvolunteermap.features.campaign.presentation.form.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormBorder
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormSecondaryText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerField(
    label: String,
    value: String,
    placeholder: String,
    onDateSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Box(modifier = Modifier.fillMaxWidth().clickable { showDialog = true }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = FormPrimaryText,
                disabledLabelColor = FormSecondaryText,
                disabledBorderColor = FormBorder,
                disabledPlaceholderColor = FormSecondaryText,
                disabledContainerColor = Color.Transparent
            ),
            singleLine = true
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(dateFormat.format(Date(millis)))
                    }
                    showDialog = false
                }) {
                    Text("Chọn", color = FormPrimaryText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy", color = FormSecondaryText)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
