package com.example.uitvolunteermap.features.post.presentation.addpost

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes

internal val PopupTop = Color(0xFFDDF3F8)
internal val PopupBottom = Color(0xFFF9FDFF)
internal val PopupDim = Color.Black.copy(alpha = 0.28f)
internal val PopupSheet = Color(0xFFFFFFFF)
internal val PopupSheetStroke = Color(0xFFE4EAF5)
internal val PopupHandle = Color(0xFFE4EAF5)
internal val PopupPrimary = Color(0xFF0B1A3B)
internal val PopupSecondary = Color(0xFF55648A)
internal val PopupPlaceholder = Color(0xFF8A97B8)
internal val PopupInput = Color(0xFFF9FDFF)
internal val PopupUpload = Color(0xFFF4F9FF)
internal val PopupAccentSurface = Color(0xFFEFF7FF)
internal val PopupAccent = Color(0xFF2563FF)
internal val PopupAccentSecondary = Color(0xFF06B6D4)
internal val PopupCoral = Color(0xFFFF5A3C)
internal val PopupDanger = Color(0xFFE11D48)
internal val PopupDangerSurface = Color(0xFFFEE4E6)
internal val PopupSuccessSurface = Color(0xFFE6F8EA)
internal val PopupWarning = Color(0xFFFCB847)

@Composable
internal fun PopupField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
    ) {
        Text(
            text = label,
            color = PopupPrimary,
            style = MaterialTheme.typography.labelLarge
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
    enabled: Boolean,
    testTag: String? = null,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = PopupPrimary),
        cursorBrush = SolidColor(PopupAccent),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minHeight)
                    .clip(RoundedCornerShape(Shapes.Radius18))
                    .background(PopupInput)
                    .border(
                        width = 1.dp,
                        color = PopupAccent.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(Shapes.Radius18)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = PopupPlaceholder,
                        style = MaterialTheme.typography.bodyMedium
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
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(PopupAccentSurface)
            .border(1.dp, PopupAccent.copy(alpha = 0.14f), RoundedCornerShape(Shapes.RadiusPill))
            .clickable(enabled = enabled, onClick = onRemove)
            .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = PopupPrimary,
            style = MaterialTheme.typography.labelLarge
        )
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = PopupCoral,
            modifier = Modifier.size(16.dp)
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
            .clip(RoundedCornerShape(Shapes.Radius18))
            .background(PopupAccentSurface)
            .border(1.dp, PopupAccent.copy(alpha = 0.14f), RoundedCornerShape(Shapes.Radius18))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(Shapes.Radius18))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.Spacing4)
                .size(22.dp)
                .clip(CircleShape)
                .background(PopupCoral)
                .clickable(enabled = enabled, onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Xóa ảnh",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
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
    onRegenerate: () -> Unit,
    onApply: () -> Unit,
    onModeChanged: (CaptionMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Shapes.Radius18))
            .background(PopupAccentSurface)
            .border(
                1.dp,
                PopupAccent.copy(alpha = 0.14f),
                RoundedCornerShape(Shapes.Radius18)
            )
            .padding(Dimens.Spacing14),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = PopupAccent,
                    modifier = Modifier.size(Dimens.IconSmall)
                )
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing2)) {
                    Text(
                        text = "Gợi ý AI cho bài viết",
                        color = PopupPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = captionModeLabel(captionMode, suggestion),
                        color = PopupSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = PopupAccent
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
                color = PopupPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = suggestion.content,
                color = PopupPrimary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )
            if (suggestion.hashtags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)
                ) {
                    suggestion.hashtags.forEach { tag ->
                        Text(
                            text = tag,
                            color = PopupAccent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(Shapes.RadiusPill))
                                .border(
                                    1.dp,
                                    PopupAccent,
                                    RoundedCornerShape(Shapes.RadiusPill)
                                )
                                .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing4)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(Shapes.Radius16))
                        .background(PopupAccent)
                        .clickable(onClick = onApply),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Áp dụng",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(Shapes.Radius16))
                        .border(1.dp, PopupAccent, RoundedCornerShape(Shapes.Radius16))
                        .clickable(enabled = !isGenerating, onClick = onRegenerate),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tạo lại",
                        color = PopupAccent,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        CaptionModeSwitch(
            current = captionMode,
            gemmaAvailable = gemmaModelAvailable,
            enabled = !isGenerating,
            onChange = onModeChanged
        )
    }
}

@Composable
private fun CaptionModeSwitch(
    current: CaptionMode,
    gemmaAvailable: Boolean,
    enabled: Boolean,
    onChange: (CaptionMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Shapes.RadiusPill))
                .border(1.dp, PopupAccent.copy(alpha = 0.18f), RoundedCornerShape(Shapes.RadiusPill))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CaptionModeChip(
                label = "Nhanh (template)",
                selected = current == CaptionMode.TEMPLATE_FAST,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = { onChange(CaptionMode.TEMPLATE_FAST) }
            )
            CaptionModeChip(
                label = "AI viết lại",
                selected = current == CaptionMode.VL_GEMMA,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = { onChange(CaptionMode.VL_GEMMA) }
            )
        }
        if (!gemmaAvailable) {
            Text(
                text = "Chế độ AI cần model on-device. Đặt qwen.task vào " +
                    "/sdcard/Android/data/com.example.uitvolunteermap/files/llm/ để bật. " +
                    "Chưa có model vẫn dùng được chế độ Nhanh.",
                color = PopupSecondary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CaptionModeChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(if (selected) PopupAccent else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else PopupAccent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun captionModeLabel(
    mode: CaptionMode,
    suggestion: CaptionSuggestion?
): String = when (mode) {
    CaptionMode.TEMPLATE_FAST -> "ML Kit + Template UIT (nhanh)"
    CaptionMode.VL_GEMMA -> if (suggestion?.refinedByLlm == true) {
        "ML Kit + AI viết lại (on-device)"
    } else {
        "Đang dùng AI on-device…"
    }
}
