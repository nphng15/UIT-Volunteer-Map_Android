package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.TextSize

@Composable
internal fun LoginFooter(modifier: Modifier = Modifier) {
    Text(
        text = "",
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.Spacing24),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontTokens.Form,
            fontSize = TextSize.Size13,
            fontWeight = FontWeight.Medium,
        ),
        color = Color(0xFF6D6C6A),
        textAlign = TextAlign.Center,
    )
}
