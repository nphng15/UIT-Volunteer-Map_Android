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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    val likeCount = estimateLikes(post.id)
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
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PostsScreenMuted),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialsOf(post.authorName),
                        color = PostsScreenAccent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing2)) {
                    Text(
                        text = post.authorName,
                        color = PostsScreenPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${post.teamName} • ${post.publishedAt}",
                        color = PostsScreenSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            if (isExpanded) {
                PostMetaPill(
                    label = "Chi tiet",
                    containerColor = PostsScreenAccentSoft,
                    textColor = PostsScreenAccent
                )
            }
        }

        Text(
            text = post.title,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            maxLines = if (isExpanded) 4 else 3,
            overflow = TextOverflow.Ellipsis
        )

        if (imageRequest != null) {
            AsyncImage(
                model = imageRequest,
                contentDescription = "Anh bai viet",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(Shapes.Radius24))
                    .background(PostsScreenMuted)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(Shapes.Radius24))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PostsScreenAccentSoft, PostsScreenMuted)
                        )
                    )
                    .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius24)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No image",
                    color = PostsScreenSecondary,
                    style = MaterialTheme.typography.labelLarge
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = PostsScreenPrimary
                )
                Text(
                    text = "$likeCount",
                    color = PostsScreenPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
            ) {
                Text(
                    text = if (isExpanded) "Thu gon" else "Xem them",
                    color = PostsScreenAccent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                if (canManagePost) {
                    TextButton(onClick = onEditClick) {
                        Text(
                            text = "Sua",
                            color = PostsScreenAccent,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = onDeleteClick) {
                        Text(
                            text = "Xoa",
                            color = PostsScreenDanger,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostMetaPill(
    label: String,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(containerColor)
            .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing6)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun initialsOf(fullName: String): String {
    val parts = fullName.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
    if (parts.isEmpty()) {
        return "U"
    }
    return parts.takeLast(2)
        .joinToString(separator = "") { it.take(1).uppercase() }
}

private fun estimateLikes(postId: Int): Int {
    return 18 + ((postId * 11) % 67)
}
