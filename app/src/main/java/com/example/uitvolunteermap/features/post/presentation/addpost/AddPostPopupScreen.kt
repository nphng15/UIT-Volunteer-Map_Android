package com.example.uitvolunteermap.features.post.presentation.addpost

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.SecondaryPillButton

@OptIn(ExperimentalLayoutApi::class)
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

@OptIn(ExperimentalLayoutApi::class)
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
                    text = "Tài khoản khách không có quyền tạo bài viết. Hãy đăng nhập bằng tài khoản trưởng nhóm.",
                    color = PopupDanger,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

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
                    enabled = state.canManagePosts && !state.isSubmitting,
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
                    enabled = state.canManagePosts && !state.isSubmitting,
                    testTag = VolunteerFlowTestTags.AddPostContentField,
                    onValueChange = { onEvent(AddPostPopupUiEvent.ContentChanged(it)) }
                )
            }

            PopupField(label = "Ảnh đính kèm") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
                    ) {
                        val previewSlots = state.attachmentNames.take(3)
                        previewSlots.forEach { name ->
                            ImageSlot(
                                label = name.take(6).uppercase(),
                                selected = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (previewSlots.size < 3) {
                            repeat(3 - previewSlots.size) {
                                ImageSlot(
                                    label = (it + previewSlots.size + 1).toString(),
                                    selected = false,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                                .testTag(VolunteerFlowTestTags.AddPostUploadButton)
                                .clip(RoundedCornerShape(Shapes.Radius18))
                                .background(PopupUpload)
                                .border(
                                    1.dp,
                                    PopupAccent.copy(alpha = 0.14f),
                                    RoundedCornerShape(Shapes.Radius18)
                                )
                                .clickable(enabled = state.canManagePosts && !state.isSubmitting) {
                                    onEvent(AddPostPopupUiEvent.UploadClicked)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
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
                                Text(
                                    text = "Thêm",
                                    color = PopupPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (state.attachmentNames.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
                        ) {
                            state.attachmentNames.forEachIndexed { index, attachmentName ->
                                AttachmentChip(
                                    name = attachmentName,
                                    enabled = state.canManagePosts && !state.isSubmitting,
                                    onRemove = {
                                        onEvent(AddPostPopupUiEvent.RemoveAttachmentClicked(index))
                                    }
                                )
                            }
                        }
                    }
                }
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
        ) {
            SecondaryPillButton(
                label = if (state.canManagePosts) "Hủy" else "Đóng",
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
                        if (state.canManagePosts) {
                            if (state.isSubmitting) {
                                PopupAccent.copy(alpha = 0.72f)
                            } else {
                                PopupAccent
                            }
                        } else {
                            PopupSheetStroke
                        }
                    )
                    .clickable(enabled = state.canManagePosts && !state.isSubmitting) {
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
                        color = if (state.canManagePosts) Color.White else PopupSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageSlot(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(Shapes.Radius18))
            .background(if (selected) PopupAccentSurface else PopupInput)
            .border(
                1.dp,
                if (selected) PopupAccent.copy(alpha = 0.14f) else PopupAccent.copy(alpha = 0.10f),
                RoundedCornerShape(Shapes.Radius18)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) PopupPrimary else PopupSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
