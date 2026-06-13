package com.example.uitvolunteermap.features.campaign.presentation.detail.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignDetailPostUiModel
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.CardShape
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenAccent
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenAccentPressed
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBorder
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurface
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurfaceRaised
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurfaceVariant
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextMuted
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextSecondary

@Composable
internal fun CampaignPostsBlock(
    posts: List<CampaignDetailPostUiModel>,
    canManageCampaigns: Boolean = false,
    onViewAllPosts: () -> Unit,
    onPostClick: (Int) -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BÀI VIẾT MỚI",
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "→ TẤT CẢ BÀI VIẾT",
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .testTag(VolunteerFlowTestTags.CampaignDetailViewAllPosts)
                    .clickable(onClick = onViewAllPosts)
            )
        }

        if (posts.isEmpty()) {
            Text(
                text = "Chưa có bài viết nào cho chiến dịch này.",
                color = ScreenTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            posts.forEach { post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ScreenBorder, CardShape)
                        .background(ScreenSurface, CardShape)
                        .clickable { onPostClick(post.id) }
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(174.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    post.accentColors.toGradientColors(
                                        fallback = listOf(ScreenSurfaceVariant, ScreenSurfaceRaised, ScreenSurface)
                                    )
                                )
                            )
                            .border(1.dp, ScreenBorder, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ẢNH BÀI VIẾT · ${post.id}",
                            color = ScreenTextPrimary.copy(alpha = 0.42f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.teamName,
                            color = ScreenPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.publishedAt,
                            color = ScreenTextMuted,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = post.title,
                        color = ScreenTextPrimary,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            lineHeight = 24.sp
                        ),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = post.summary,
                        color = ScreenTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Đọc thêm ↓",
                            color = ScreenAccentPressed,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (canManageCampaigns) {
                            Text(
                                text = "Sửa",
                                color = ScreenTextMuted,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onEditClick() }
                            )
                            Text(
                                text = "Xóa",
                                color = ScreenAccent,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onDeleteClick() }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun List<Long>.toGradientColors(fallback: List<Color>): List<Color> {
    return if (isNotEmpty()) {
        map { Color(it) }
    } else {
        fallback
    }
}
