package com.example.uitvolunteermap.features.campaign.presentation.form

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val FormTopBackground = Color(0xFFF7F1D8)
private val FormContentBackground = Color(0xFFFFFDF9)
private val FormBorder = Color(0xFFE7DED3)
private val FormPrimaryText = Color(0xFF1F1A17)
private val FormSecondaryText = Color(0xFF625750)
private val FormAccentSoft = Color(0xFFFFF4CC)
private val FormErrorText = Color(0xFFB00020)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignFormScreen(
    state: CampaignFormUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignFormUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept hardware/gesture back khi có thay đổi chưa lưu
    BackHandler(enabled = state.isDirty && !state.showDiscardDialog) {
        onEvent(CampaignFormUiEvent.BackClicked)
    }

    if (state.showDiscardDialog) {
        DiscardChangesDialog(
            onConfirm = { onEvent(CampaignFormUiEvent.DiscardConfirmed) },
            onDismiss = { onEvent(CampaignFormUiEvent.DiscardCancelled) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = FormContentBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state.mode) {
                            CampaignFormMode.Create -> "Tạo chiến dịch"
                            CampaignFormMode.Edit -> "Chỉnh sửa chiến dịch"
                        },
                        color = FormPrimaryText,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CampaignFormUiEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = FormPrimaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FormTopBackground)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Preload spinner (Edit mode đang tải dữ liệu ban đầu)
                state.isLoadingPreload -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // Preload thất bại và không có data — hiện lỗi + retry
                state.errorMessage != null && state.campaignName.isEmpty() && state.mode == CampaignFormMode.Edit -> {
                    PreloadErrorState(
                        message = state.errorMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    FormBody(state = state, onEvent = onEvent)
                }
            }
        }
    }
}

@Composable
private fun FormBody(
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

        // Validation / conflict / network error
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

@Composable
private fun FormField(
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

@Composable
private fun DiscardChangesDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Hủy thay đổi?",
                fontWeight = FontWeight.Bold,
                color = FormPrimaryText
            )
        },
        text = {
            Text(
                text = "Bạn có thay đổi chưa được lưu. Nếu thoát, các thay đổi này sẽ bị mất.",
                color = FormSecondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Hủy thay đổi", color = FormErrorText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Tiếp tục chỉnh sửa", color = FormPrimaryText)
            }
        },
        containerColor = FormContentBackground
    )
}

@Composable
private fun PreloadErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = FormPrimaryText, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
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
                disabledPlaceholderColor = FormSecondaryText
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
