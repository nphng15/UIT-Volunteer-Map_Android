package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostCardUiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CampaignPostCard(
    post: CampaignPostCardUiModel,
    isExpanded: Boolean,
    canManagePost: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val imageRequest = remember(post.thumbnailUrl, context) {
        post.thumbnailUrl?.let { imageUrl ->
            ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build()
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = Dimens.Spacing20)
            .clip(RoundedCornerShape(Shapes.Radius28))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius28))
            .clickable(onClick = onClick)
            .padding(Dimens.Spacing14),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Shapes.RadiusPill))
                        .background(PostsScreenAccentSoft)
                        .border(
                            1.dp,
                            PostsScreenAccent.copy(alpha = 0.18f),
                            RoundedCornerShape(Shapes.RadiusPill)
                        )
                        .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing4)
                ) {
                    Text(
                        text = post.teamName.uppercase(),
                        color = PostsScreenAccent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = post.publishedAt.uppercase(),
                    color = PostsScreenSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isExpanded) {
                MiniActionPill(
                    label = "MỞ RỘNG",
                    containerColor = PostsScreenAccentSoft,
                    contentColor = PostsScreenAccent
                )
            }
        }

        Text(
            text = post.title,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = if (isExpanded) 4 else 3,
            overflow = TextOverflow.Ellipsis
        )

        if (imageRequest != null) {
            AsyncImage(
                model = imageRequest,
                contentDescription = "Ảnh bài viết",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(208.dp)
                    .clip(RoundedCornerShape(Shapes.Radius24))
                    .background(PostsScreenMuted)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(Shapes.Radius24))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PostsScreenAccentSoft, PostsScreenPanelSoft)
                        )
                    )
                    .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius24)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có ảnh đính kèm",
                    color = PostsScreenSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            text = if (isExpanded) post.content else post.excerpt,
            color = PostsScreenSecondary,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) 8 else 3,
            overflow = TextOverflow.Ellipsis
        )

        if (isExpanded && post.attachmentLabels.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
            ) {
                post.attachmentLabels.forEach { attachmentName ->
                    AttachmentChip(
                        name = attachmentName,
                        removable = false,
                        onRemove = {}
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = PostsScreenSecondary
                )
                Text(
                    text = "${estimateLikes(post.id)}",
                    color = PostsScreenSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostTextAction(
                    label = if (isExpanded) "Thu gọn" else "Đọc thêm",
                    color = PostsScreenAccent,
                    onClick = onClick
                )
                if (canManagePost) {
                    PostTextAction(
                        label = "Sửa",
                        color = PostsScreenAccent,
                        onClick = onEditClick
                    )
                    PostTextAction(
                        label = "Xóa",
                        color = PostsScreenCoral,
                        onClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniActionPill(
    label: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(containerColor)
            .border(1.dp, contentColor.copy(alpha = 0.12f), RoundedCornerShape(Shapes.RadiusPill))
            .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing4)
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PostTextAction(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

private fun estimateLikes(postId: Int): Int {
    return 18 + ((postId * 11) % 67)
}
