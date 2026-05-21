package com.example.uitvolunteermap.features.post.presentation.addpost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
