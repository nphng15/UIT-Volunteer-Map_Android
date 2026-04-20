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
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPostPopupScreen(
    state: AddPostPopupUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddPostPopupUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetShape = RoundedCornerShape(
        topStart = 30.dp,
        topEnd = 30.dp,
        bottomStart = Shapes.Radius24,
        bottomEnd = Shapes.Radius24
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
                        colors = listOf(PopupTop, PopupBottom)
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
                    .widthIn(max = 360.dp)
                    .fillMaxWidth()
                    .testTag(VolunteerFlowTestTags.AddPostPopupScreen)
                    .padding(horizontal = Dimens.Spacing20)
                    .clip(sheetShape)
                    .background(PopupSheet)
                    .border(1.dp, PopupSheetStroke, sheetShape)
                    .padding(
                        start = Dimens.Spacing16,
                        top = Dimens.Spacing12,
                        end = Dimens.Spacing16,
                        bottom = Dimens.Spacing16
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 52.dp, height = 6.dp)
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
                        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)
                    ) {
                        Text(
                            text = "Them bai viet",
                            color = PopupPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Cap nhat nhanh tien do hoat dong theo bo cuc bang tin.",
                            color = PopupSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = Dimens.Spacing10)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PopupAccentSurface)
                            .clickable(enabled = !state.isSubmitting) {
                                onEvent(AddPostPopupUiEvent.CloseClicked)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Dong popup",
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
                            .background(PopupUpload)
                            .border(1.dp, PopupSheetStroke, RoundedCornerShape(Shapes.Radius18))
                            .padding(Dimens.Spacing14)
                    ) {
                        Text(
                            text = "Tai khoan guest khong co quyen tao bai viet. Hay dang nhap bang role leader.",
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
                    PopupField(label = "Tieu de bai viet") {
                        PopupTextInput(
                            value = state.title,
                            placeholder = "Nhap tieu de cho bai viet",
                            minHeight = 52.dp,
                            singleLine = true,
                            enabled = state.canManagePosts && !state.isSubmitting,
                            testTag = VolunteerFlowTestTags.AddPostTitleField,
                            onValueChange = { onEvent(AddPostPopupUiEvent.TitleChanged(it)) }
                        )
                    }

                    PopupField(label = "Noi dung mo ta") {
                        PopupTextInput(
                            value = state.content,
                            placeholder = "Tom tat dien bien, ket qua va thong tin can truyen thong.",
                            minHeight = 96.dp,
                            singleLine = false,
                            enabled = state.canManagePosts && !state.isSubmitting,
                            testTag = VolunteerFlowTestTags.AddPostContentField,
                            onValueChange = { onEvent(AddPostPopupUiEvent.ContentChanged(it)) }
                        )
                    }

                    PopupField(label = "Anh dinh kem") {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(102.dp)
                                    .testTag(VolunteerFlowTestTags.AddPostUploadButton)
                                    .clip(RoundedCornerShape(Shapes.Radius22))
                                    .background(PopupUpload)
                                    .border(
                                        width = 1.dp,
                                        color = PopupSheetStroke,
                                        shape = RoundedCornerShape(Shapes.Radius22)
                                    )
                                    .clickable(enabled = state.canManagePosts && !state.isSubmitting) {
                                        onEvent(AddPostPopupUiEvent.UploadClicked)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(PopupAccentSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = PopupAccent,
                                            modifier = Modifier.size(Dimens.IconMedium)
                                        )
                                    }
                                    Text(
                                        text = "Them anh hoac poster",
                                        color = PopupPrimary,
                                        style = MaterialTheme.typography.labelLarge,
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
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
                                ) {
                                    state.attachmentNames.forEachIndexed { index, attachmentName ->
                                        AttachmentChip(
                                            name = attachmentName,
                                            enabled = state.canManagePosts && !state.isSubmitting,
                                            onRemove = {
                                                onEvent(
                                                    AddPostPopupUiEvent.RemoveAttachmentClicked(
                                                        index
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
                            color = PopupDanger,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag(VolunteerFlowTestTags.AddPostPublishButton)
                            .clip(RoundedCornerShape(Shapes.Radius18))
                            .background(
                                if (state.canManagePosts) {
                                    if (state.isSubmitting) PopupAccent.copy(alpha = 0.72f) else PopupAccent
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
                                text = "Dang bai",
                                color = if (state.canManagePosts) Color.White else PopupSecondary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
