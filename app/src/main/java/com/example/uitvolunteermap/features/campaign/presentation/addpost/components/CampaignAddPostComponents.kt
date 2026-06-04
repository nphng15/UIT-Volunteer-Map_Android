package com.example.uitvolunteermap.features.campaign.presentation.addpost.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupOrange
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupInput
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupLabel
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupPlaceholder
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSecondary
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSheetStroke
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupText
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupUpload
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupUploadStroke

@Composable
internal fun PopupField(
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
internal fun PopupTextInput(
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
internal fun AttachmentChip(
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

@Composable
internal fun PickedImageThumb(
    uri: Uri,
    label: String,
    enabled: Boolean,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PopupUpload)
            .border(1.dp, PopupUploadStroke, RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(PopupOrange)
                .clickable(enabled = enabled, onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "x",
                color = PopupUpload,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AiSuggestionCard(
    isGenerating: Boolean,
    suggestion: CaptionSuggestion?,
    onRegenerate: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PopupUpload)
            .border(1.dp, PopupUploadStroke, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Gợi ý AI cho bài viết",
                    color = PopupLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "ML Kit phân tích ảnh + viết theo phong cách UIT",
                    color = PopupSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = PopupOrange
                )
            }
        }

        if (isGenerating && suggestion == null) {
            Text(
                text = "Đang phân tích ảnh để gợi ý tiêu đề và nội dung…",
                color = PopupSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (suggestion != null) {
            Text(
                text = suggestion.title,
                color = PopupLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = suggestion.content,
                color = PopupLabel,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
            if (suggestion.hashtags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestion.hashtags.forEach { tag ->
                        Text(
                            text = tag,
                            color = PopupOrange,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .border(
                                    1.dp,
                                    PopupOrange,
                                    RoundedCornerShape(999.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PopupOrange)
                        .clickable(onClick = onApply),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Áp dụng",
                        color = PopupUpload,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, PopupOrange, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isGenerating, onClick = onRegenerate),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tạo lại",
                        color = PopupOrange,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    }
}
