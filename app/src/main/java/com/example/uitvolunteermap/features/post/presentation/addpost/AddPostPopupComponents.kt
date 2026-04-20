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

internal val PopupTop = Color(0xFFF7F1D8)
internal val PopupBottom = Color(0xFFFFFDF9)
internal val PopupDim = Color.Black.copy(alpha = 0.22f)
internal val PopupSheet = Color.White.copy(alpha = 0.98f)
internal val PopupSheetStroke = Color(0xFFE7DED3)
internal val PopupHandle = Color(0xFFD9D0C2)
internal val PopupPrimary = Color(0xFF121212)
internal val PopupSecondary = Color(0xFF6B7280)
internal val PopupPlaceholder = Color(0xFF9CA3AF)
internal val PopupInput = Color(0xFFFFFDF9)
internal val PopupUpload = Color(0xFFF7F3EA)
internal val PopupAccentSurface = Color(0xFFF3E8C1)
internal val PopupAccent = Color(0xFF1F3673)
internal val PopupDanger = Color(0xFFB54747)

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
                        color = PopupSheetStroke,
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
            .background(PopupAccentSurface.copy(alpha = 0.45f))
            .border(1.dp, PopupSheetStroke, RoundedCornerShape(Shapes.RadiusPill))
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
            tint = PopupSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}
