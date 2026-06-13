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
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.AttachmentChip
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
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.PopupField
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.PopupTextInput

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
