package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.Dimens

@Composable
internal fun LoginScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing18 = Dimens.Spacing16 + Dimens.Spacing2

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ColorTokens.ScreenBackgroundTop,
                        ColorTokens.ScreenBackgroundBottom,
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = Dimens.Spacing24,
                vertical = spacing18,
            ),
        content = content,
    )
}
