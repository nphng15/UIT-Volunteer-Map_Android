package com.example.uitvolunteermap.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.Shapes

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontTokens.Body, // Dùng Token thay cho biến local
                color = ColorTokens.TextSecondary, // Dùng Token thay cho Color hex
            ),
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing8))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    // Sử dụng màu nền từ Theme, tránh hardcode trắng ngà
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(Shapes.Radius16), // Dùng Shapes Token
                )
                .border(
                    width = 1.dp,
                    // Dùng MaterialTheme.colorScheme.error để đồng bộ logic báo lỗi
                    color = if (error != null) MaterialTheme.colorScheme.error else ColorTokens.BorderSubtle,
                    shape = RoundedCornerShape(Shapes.Radius16),
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = readOnly,
                textStyle = TextStyle(
                    fontFamily = FontTokens.Form, // Đồng bộ với Outfit font đã khai báo
                    fontWeight = FontWeight.Normal,
                    color = if (readOnly) ColorTokens.TextSecondary else MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
        if (error != null) {
            Spacer(modifier = Modifier.height(Dimens.Spacing4)) // Dùng Dimens Token
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}