package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.core.ui.theme.Dimens

@Composable
internal fun LoginHeroHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp),
    ) {
        Text(
            text = "UIT • TÌNH NGUYỆN",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.3.sp,
            ),
            color = LoginPalette.Primary,
        )

        Text(
            text = buildAnnotatedString {
                append("Ghi tên\ncho ")
                withStyle(
                    SpanStyle(
                        background = LoginPalette.CampaignSun.copy(alpha = 0.65f),
                        color = LoginPalette.TextPrimary,
                    )
                ) {
                    append("một")
                }
                append("\nmùa hè.")
            },
            modifier = Modifier.padding(top = Dimens.Spacing12),
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 52.sp,
                lineHeight = 46.sp,
                letterSpacing = (-1.5).sp,
            ),
            color = LoginPalette.TextPrimary,
        )

        Text(
            text = "Nền tảng quản lý chiến dịch tình nguyện dành cho sinh viên UIT và các đội hình chiến dịch.",
            modifier = Modifier
                .padding(top = Dimens.Spacing16)
                .widthIn(max = 300.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 22.sp,
            ),
            color = LoginPalette.TextSecondary,
        )
    }
}
