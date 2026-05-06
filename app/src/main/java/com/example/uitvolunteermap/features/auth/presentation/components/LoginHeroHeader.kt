package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Elevations
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.TextSize

@Composable
internal fun LoginHeroHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.Spacing8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontTokens.Form,
                fontSize = TextSize.Size26,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp,
            ),
            color = Color(0xFF1A1918),
        )


        Box(
            modifier = Modifier
                .size(Dimens.Avatar + Dimens.Spacing32)
                .shadow(
                    elevation = Elevations.Level12,
                    shape = CircleShape,
                    ambientColor = Color(0x141A1918),
                    spotColor = Color(0x141A1918),
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC8F0D8),
                            Color(0xFFD89575),
                        )
                    ),
                    shape = CircleShape,
                ),
        )
    }
}
