package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes

@Composable
internal fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = PostsScreenPrimary
        )
    }
}

@Composable
internal fun CreatePostButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenAccent.copy(alpha = 0.22f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Them bai viet",
            tint = PostsScreenAccent
        )
    }
}

@Composable
internal fun AttachmentChip(
    name: String,
    removable: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(PostsScreenPanelSoft)
            .border(
                1.dp,
                if (removable) PostsScreenAccentSecondary.copy(alpha = 0.18f) else PostsScreenBorder,
                RoundedCornerShape(Shapes.RadiusPill)
            )
            .then(
                if (removable) {
                    Modifier.clickable(onClick = onRemove)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.labelLarge
        )
        if (removable) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Xóa ảnh",
                tint = PostsScreenCoral,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
internal fun PrimaryPillButton(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(if (enabled) PostsScreenAccent else PostsScreenAccent.copy(alpha = 0.42f))
            .border(1.dp, PostsScreenAccent.copy(alpha = 0.18f), RoundedCornerShape(Shapes.RadiusPill))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Dimens.Spacing16, vertical = Dimens.Spacing12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun SecondaryPillButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.RadiusPill))
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Spacing16, vertical = Dimens.Spacing12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
