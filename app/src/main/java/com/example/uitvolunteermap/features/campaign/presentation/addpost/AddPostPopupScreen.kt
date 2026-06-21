package com.example.uitvolunteermap.features.campaign.presentation.addpost

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.AiSuggestionCard
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupBackdropBottom
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupBackdropTop
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupClose
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupDim
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupLabel
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupMuted
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupOrange
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSecondary
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSheet
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSheetStroke
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupText
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupUpload
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupUploadStroke
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupYellow
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.PickedImageThumb
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.PopupField
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.PopupTextInput

private const val MAX_IMAGES = 5

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

    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGES)
    ) { uris ->
        if (uris.isNotEmpty()) onEvent(AddPostPopupUiEvent.ImagesPicked(uris))
    }
    val singlePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onEvent(AddPostPopupUiEvent.ImagesPicked(listOf(uri)))
    }
    val launchPicker: () -> Unit = {
        val remaining = MAX_IMAGES - state.pickedImages.size
        when {
            remaining <= 0 -> Unit
            remaining == 1 -> singlePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            else -> pickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

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
                            text = "Thêm bài viết",
                            color = PopupText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Tạo bài viết ngắn để cập nhật hoạt động của đội hình.",
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
                    PopupField(label = "Tiêu đề bài viết") {
                        PopupTextInput(
                            value = state.title,
                            placeholder = "Nhập tiêu đề cho bài viết",
                            minHeight = 52.dp,
                            singleLine = true,
                            onValueChange = { onEvent(AddPostPopupUiEvent.TitleChanged(it)) }
                        )
                    }

                    PopupField(label = "Nội dung mô tả") {
                        PopupTextInput(
                            value = state.content,
                            placeholder = "Mô tả nhanh diễn biến hoạt động, số lượng thành viên tham gia và điểm nhấn cần chia sẻ.",
                            minHeight = 92.dp,
                            singleLine = false,
                            onValueChange = { onEvent(AddPostPopupUiEvent.ContentChanged(it)) }
                        )
                    }

                    PopupField(label = "Ảnh đính kèm") {
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
                                    .clickable(
                                        enabled = !state.isSubmitting &&
                                            state.pickedImages.size < MAX_IMAGES
                                    ) { launchPicker() },
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
                                        text = if (state.pickedImages.isEmpty()) {
                                            "Chọn ảnh hoạt động từ thiết bị"
                                        } else {
                                            "Thêm ảnh (${state.pickedImages.size}/$MAX_IMAGES)"
                                        },
                                        color = PopupLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "JPG, PNG · tối đa 5 ảnh",
                                        color = PopupSecondary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            if (state.pickedImages.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(
                                        items = state.pickedImages,
                                        key = { it.uri.toString() }
                                    ) { picked ->
                                        val idx = state.pickedImages.indexOf(picked)
                                        PickedImageThumb(
                                            uri = picked.uri,
                                            label = picked.fileName,
                                            enabled = !state.isSubmitting,
                                            onRemove = {
                                                onEvent(
                                                    AddPostPopupUiEvent.RemovePickedImageClicked(idx)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.pickedImages.isNotEmpty()) {
                        AiSuggestionCard(
                            isGenerating = state.isGeneratingCaption,
                            suggestion = state.captionSuggestion,
                            onRegenerate = {
                                onEvent(AddPostPopupUiEvent.RegenerateCaptionClicked)
                            },
                            onApply = {
                                onEvent(AddPostPopupUiEvent.AcceptSuggestionClicked)
                            }
                        )
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
                                text = "Đăng bài",
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
