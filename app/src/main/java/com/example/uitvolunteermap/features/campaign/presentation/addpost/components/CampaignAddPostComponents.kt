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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import coil.compose.AsyncImage
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode
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
    captionMode: CaptionMode,
    gemmaModelAvailable: Boolean,
    campaignName: String,
    onCampaignNameChange: (String) -> Unit,
    onRegenerate: () -> Unit,
    onApply: () -> Unit,
    onModeChanged: (CaptionMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PopupUpload)
            .border(1.dp, PopupUploadStroke, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gợi ý AI",
                color = PopupLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold
            )
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = PopupOrange
                )
            }
        }

        CampaignContextInput(
            value = campaignName,
            placeholder = "Tên chương trình (vd: Mùa Hè Xanh 2026)",
            onValueChange = onCampaignNameChange
        )

        CampaignCaptionModeRow(
            current = captionMode,
            enabled = !isGenerating,
            onChange = onModeChanged
        )
        if (!gemmaModelAvailable && captionMode == CaptionMode.VL_GEMMA) {
            Text(
                text = "Chưa có model AI (qwen.task) — tạm dùng chế độ Nhanh.",
                color = PopupSecondary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (suggestion != null) {
            Text(
                text = suggestion.title,
                color = PopupLabel,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = suggestion.content,
                color = PopupLabel,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (suggestion.hashtags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    suggestion.hashtags.take(5).forEach { tag ->
                        Text(
                            text = tag,
                            color = PopupOrange,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
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
                        .height(36.dp)
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
                        .height(36.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, PopupOrange, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isGenerating, onClick = onRegenerate)
                        .padding(horizontal = 14.dp),
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

@Composable
private fun CampaignContextInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall.copy(color = PopupText),
        cursorBrush = SolidColor(PopupOrange),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PopupInput)
                    .border(1.dp, PopupSheetStroke, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = PopupPlaceholder,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                inner()
            }
        }
    )
}

@Composable
private fun CampaignCaptionModeRow(
    current: CaptionMode,
    enabled: Boolean,
    onChange: (CaptionMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, PopupOrange.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        CampaignCaptionModeChip(
            label = "Mẫu nhanh",
            selected = current == CaptionMode.TEMPLATE_FAST,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            onClick = { onChange(CaptionMode.TEMPLATE_FAST) }
        )
        CampaignCaptionModeChip(
            label = "Dùng AI",
            selected = current == CaptionMode.VL_GEMMA,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            onClick = { onChange(CaptionMode.VL_GEMMA) }
        )
    }
}

@Composable
private fun CampaignCaptionModeChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PopupOrange else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) PopupUpload else PopupOrange,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
