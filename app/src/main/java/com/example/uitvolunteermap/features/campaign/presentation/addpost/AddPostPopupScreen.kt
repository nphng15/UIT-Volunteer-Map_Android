package com.example.uitvolunteermap.features.campaign.presentation.addpost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val PopupBackdropTop = Color(0xFFF7F2E9)
private val PopupBackdropBottom = Color(0xFFFFFDF9)
private val PopupDim = Color(0x403A2A00)
private val PopupSheet = Color(0xFFFFFDF9)
private val PopupSheetStroke = Color(0xFFE7DED3)
private val PopupMuted = Color(0xFFD8D2C7)
private val PopupLabel = Color(0xFF20303A)
private val PopupSecondary = Color(0xFF6D839A)
private val PopupPlaceholder = Color(0xFF9AA6B2)
private val PopupInput = Color(0xFFFFF8EF)
private val PopupUpload = Color(0xFFF1F3F7)
private val PopupUploadStroke = Color(0xFFE3E6EB)
private val PopupYellow = Color(0xFFFFF4CC)
private val PopupOrange = Color(0xFFE06300)
private val PopupText = Color(0xFF121212)
private val PopupClose = Color(0xFF3A2A00)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPostPopupScreen(
    state: AddPostPopupUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddPostPopupUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetShape = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 28.dp,
        bottomEnd = 28.dp
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PopupBackdropTop, PopupBackdropBottom)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PopupDim)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 350.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(sheetShape)
                    .background(PopupSheet)
                    .border(1.dp, PopupSheetStroke, sheetShape)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 52.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(PopupMuted)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Them bai viet",
                            color = PopupText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Tao bai viet ngan de cap nhat hoat dong cua doi hinh.",
                            color = PopupSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PopupYellow)
                            .clickable(enabled = !state.isSubmitting) {
                                onEvent(AddPostPopupUiEvent.CloseClicked)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "x",
                            color = PopupClose,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PopupField(label = "Tieu de bai viet") {
                        PopupTextInput(
                            value = state.title,
                            placeholder = "Nhap tieu de cho bai viet",
                            minHeight = 52.dp,
                            singleLine = true,
                            onValueChange = { onEvent(AddPostPopupUiEvent.TitleChanged(it)) }
                        )
                    }

                    PopupField(label = "Noi dung mo ta") {
                        PopupTextInput(
                            value = state.content,
                            placeholder = "Mo ta nhanh dien bien hoat dong, so luong thanh vien tham gia va diem nhan can chia se.",
                            minHeight = 92.dp,
                            singleLine = false,
                            onValueChange = { onEvent(AddPostPopupUiEvent.ContentChanged(it)) }
                        )
                    }

                    PopupField(label = "Anh dinh kem") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(104.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(PopupUpload)
                                    .border(
                                        width = 1.dp,
                                        color = PopupUploadStroke,
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable(enabled = !state.isSubmitting) {
                                        onEvent(AddPostPopupUiEvent.UploadClicked)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(PopupYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+",
                                            color = PopupOrange,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Text(
                                        text = "Them anh hoac poster hoat dong",
                                        color = PopupLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "JPG, PNG - toi da 5 anh",
                                        color = PopupSecondary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            if (state.attachmentNames.isNotEmpty()) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.attachmentNames.forEach { attachmentName ->
                                        AttachmentChip(
                                            name = attachmentName,
                                            enabled = !state.isSubmitting,
                                            onRemove = {
                                                onEvent(
                                                    AddPostPopupUiEvent.RemoveAttachmentClicked(
                                                        attachmentName
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = PopupOrange,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (state.isSubmitting) {
                                    PopupYellow.copy(alpha = 0.7f)
                                } else {
                                    PopupYellow
                                }
                            )
                            .clickable(enabled = !state.isSubmitting) {
                                onEvent(AddPostPopupUiEvent.PublishClicked)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = PopupClose
                            )
                        } else {
                            Text(
                                text = "Dang bai",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PopupField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = PopupLabel,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun PopupTextInput(
    value: String,
    placeholder: String,
    minHeight: Dp,
    singleLine: Boolean,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = PopupText,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minHeight)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PopupInput)
                    .border(
                        width = 1.dp,
                        color = PopupSheetStroke,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = PopupPlaceholder,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun AttachmentChip(
    name: String,
    enabled: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PopupUpload)
            .border(1.dp, PopupUploadStroke, RoundedCornerShape(999.dp))
            .clickable(enabled = enabled, onClick = onRemove)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = PopupLabel,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "x",
            color = PopupSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
