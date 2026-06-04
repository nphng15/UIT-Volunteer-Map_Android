package com.example.uitvolunteermap.features.post.presentation.addpost

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.SecondaryPillButton

private const val MAX_IMAGES = 5

@Composable
fun AddPostPopupScreen(
    state: AddPostPopupUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddPostPopupUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        colors = listOf(PopupTop, PopupBottom)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PopupDim)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PopupAccentSurface.copy(alpha = 0.72f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8)
            ) {
                AddPostBottomSheetCard(
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
internal fun AddPostBottomSheetCard(
    state: AddPostPopupUiState,
    onEvent: (AddPostPopupUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetShape = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val multiPickerLauncher = rememberLauncherForActivityResult(
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
            else -> multiPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    Column(
        modifier = modifier
            .widthIn(max = 430.dp)
            .fillMaxWidth()
            .testTag(VolunteerFlowTestTags.AddPostPopupScreen)
            .clip(sheetShape)
            .background(PopupSheet)
            .border(1.dp, PopupSheetStroke, sheetShape)
            .padding(
                start = Dimens.Spacing16,
                top = Dimens.Spacing10,
                end = Dimens.Spacing16,
                bottom = Dimens.Spacing16
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 5.dp)
                .clip(CircleShape)
                .background(PopupHandle)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)
            ) {
                Text(
                    text = "Tạo bài viết",
                    color = PopupPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = Dimens.Spacing10)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PopupAccentSurface)
                    .border(
                        1.dp,
                        PopupAccent.copy(alpha = 0.12f),
                        CircleShape
                    )
                    .clickable(enabled = !state.isSubmitting) {
                        onEvent(AddPostPopupUiEvent.CloseClicked)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Đóng biểu mẫu",
                    tint = PopupPrimary,
                    modifier = Modifier.size(Dimens.IconSmall)
                )
            }
        }

        if (!state.canManagePosts) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Shapes.Radius18))
                    .background(PopupDangerSurface)
                    .border(
                        1.dp,
                        PopupDanger.copy(alpha = 0.16f),
                        RoundedCornerShape(Shapes.Radius18)
                    )
                    .padding(Dimens.Spacing14)
            ) {
                Text(
                    text = "Chỉ trưởng nhóm và ban tổ chức mới được tạo bài viết.",
                    color = PopupDanger,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (state.canManagePosts) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
            ) {
                PopupField(label = "Tiêu đề bài viết") {
                    PopupTextInput(
                        value = state.title,
                        placeholder = "Nhập tiêu đề cho bài viết",
                        minHeight = 52.dp,
                        singleLine = true,
                        enabled = !state.isSubmitting,
                        testTag = VolunteerFlowTestTags.AddPostTitleField,
                        onValueChange = { onEvent(AddPostPopupUiEvent.TitleChanged(it)) }
                    )
                }

                PopupField(label = "Nội dung mô tả") {
                    PopupTextInput(
                        value = state.content,
                        placeholder = "Tóm tắt diễn biến, kết quả và thông tin cần truyền thông.",
                        minHeight = 112.dp,
                        singleLine = false,
                        enabled = !state.isSubmitting,
                        testTag = VolunteerFlowTestTags.AddPostContentField,
                        onValueChange = { onEvent(AddPostPopupUiEvent.ContentChanged(it)) }
                    )
                }

                PopupField(label = "Ảnh đính kèm") {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(78.dp)
                                .testTag(VolunteerFlowTestTags.AddPostUploadButton)
                                .clip(RoundedCornerShape(Shapes.Radius18))
                                .background(PopupUpload)
                                .border(
                                    1.dp,
                                    PopupAccent.copy(alpha = 0.14f),
                                    RoundedCornerShape(Shapes.Radius18)
                                )
                                .clickable(
                                    enabled = !state.isSubmitting &&
                                        state.pickedImages.size < MAX_IMAGES
                                ) {
                                    onEvent(AddPostPopupUiEvent.UploadClicked)
                                    launchPicker()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PopupAccentSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = PopupCoral,
                                        modifier = Modifier.size(Dimens.IconSmall)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (state.pickedImages.isEmpty()) {
                                            "Chọn ảnh từ thiết bị"
                                        } else {
                                            "Thêm ảnh (${state.pickedImages.size}/$MAX_IMAGES)"
                                        },
                                        color = PopupPrimary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "JPG, PNG · tối đa $MAX_IMAGES ảnh",
                                        color = PopupSecondary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        if (state.pickedImages.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
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
                                                AddPostPopupUiEvent.RemoveAttachmentClicked(idx)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Shapes.Radius18))
                            .background(PopupDangerSurface)
                            .border(
                                1.dp,
                                PopupDanger.copy(alpha = 0.16f),
                                RoundedCornerShape(Shapes.Radius18)
                            )
                            .padding(Dimens.Spacing12)
                    ) {
                        Text(
                            text = state.errorMessage,
                            color = PopupDanger,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (state.canManagePosts) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
            ) {
                SecondaryPillButton(
                    label = "Hủy",
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(AddPostPopupUiEvent.CloseClicked) }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag(VolunteerFlowTestTags.AddPostPublishButton)
                        .clip(RoundedCornerShape(Shapes.Radius18))
                        .background(
                            if (state.isSubmitting) {
                                PopupAccent.copy(alpha = 0.72f)
                            } else {
                                PopupAccent
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
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Đăng bài",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            SecondaryPillButton(
                label = "Đóng",
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(AddPostPopupUiEvent.CloseClicked) }
            )
        }
    }
}
