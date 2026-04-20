package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.core.ui.theme.TextSize

@Composable
internal fun LoginInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation,
    error: String?,
    fieldTag: String? = null,
) {
    val inputShape = RoundedCornerShape(Shapes.Radius16)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(inputShape)
            .background(Color(0xFFFAFAF8))
            .border(
                width = Dimens.Spacing2 / 2,
                color = Color(0xFFE5E4E1),
                shape = inputShape,
            )
            .padding(
                horizontal = Dimens.Spacing16,
                vertical = Dimens.Spacing14,
            ),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = FontTokens.Form,
                fontSize = TextSize.Size12,
                fontWeight = FontWeight.Medium,
            ),
            color = Color(0xFF6D6C6A),
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fieldTag != null) Modifier.testTag(fieldTag) else Modifier),
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontTokens.Form,
                fontSize = TextSize.Size15,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1918),
            ),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = true,
            cursorBrush = SolidColor(Color(0xFF1A1918)),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontTokens.Form,
                                fontSize = TextSize.Size15,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = Color(0xFF6D6C6A),
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
