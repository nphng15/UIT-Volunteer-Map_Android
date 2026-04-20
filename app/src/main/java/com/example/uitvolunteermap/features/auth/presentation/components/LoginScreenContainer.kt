package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens

@Composable
internal fun LoginScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LoginPalette.Background, LoginPalette.BackgroundBottom),
                )
            ),
    ) {
        LoginBackgroundAccent()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.Spacing20, vertical = Dimens.Spacing16),
            content = content,
        )
    }
}

@Composable
private fun LoginBackgroundAccent(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.dp.toPx() }

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(LoginPalette.PrimarySoft, androidx.compose.ui.graphics.Color.Transparent),
                center = Offset(x = size.width * 0.18f, y = size.height * 0.08f),
                radius = size.width * 0.6f,
            ),
            radius = size.width * 0.6f,
            center = Offset(x = size.width * 0.18f, y = size.height * 0.08f),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(LoginPalette.Secondary.copy(alpha = 0.12f), androidx.compose.ui.graphics.Color.Transparent),
                center = Offset(x = size.width * 0.9f, y = size.height * 0.12f),
                radius = size.width * 0.4f,
            ),
            radius = size.width * 0.4f,
            center = Offset(x = size.width * 0.9f, y = size.height * 0.12f),
        )

        repeat(6) { index ->
            val y = 28.dp.toPx() + index * 24.dp.toPx()
            val amplitude = 8.dp.toPx() + index * 1.6f.dp.toPx()
            val path = Path().apply {
                moveTo(-24.dp.toPx(), y)
                cubicTo(
                    size.width * 0.16f,
                    y - amplitude,
                    size.width * 0.36f,
                    y + amplitude,
                    size.width * 0.55f,
                    y,
                )
                cubicTo(
                    size.width * 0.72f,
                    y - amplitude,
                    size.width * 0.9f,
                    y + amplitude,
                    size.width + 24.dp.toPx(),
                    y,
                )
            }

            drawPath(
                path = path,
                color = LoginPalette.Primary.copy(alpha = 0.14f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
    }
}
