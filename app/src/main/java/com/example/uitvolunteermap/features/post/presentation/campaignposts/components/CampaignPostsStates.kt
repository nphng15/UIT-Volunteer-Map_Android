package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsTeamUiModel

@Composable
internal fun EmptyPostsState(
    selectedTeamId: Int?,
    teams: List<CampaignPostsTeamUiModel>,
    canCreatePost: Boolean,
    onCreateClick: () -> Unit
) {
    val selectedTeamName = teams.firstOrNull { it.id == selectedTeamId }?.label

    Column(
        modifier = Modifier
            .padding(horizontal = Dimens.Spacing20)
            .clip(RoundedCornerShape(Shapes.Radius24))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius24))
            .padding(Dimens.Spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
    ) {
        Text(
            text = if (selectedTeamName == null) {
                "Chua co bai viet nao cho chien dich nay."
            } else {
                "Chua co bai viet nao cho $selectedTeamName."
            },
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (canCreatePost) {
                "Leader co the tao bai viet dau tien bang nut + o header."
            } else {
                "Tai khoan guest chi co quyen xem bai viet."
            },
            color = PostsScreenSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        if (canCreatePost) {
            PrimaryPillButton(
                label = "Tao bai viet",
                onClick = onCreateClick
            )
        }
    }
}

@Composable
internal fun DeletePostDialog(
    title: String,
    isBusy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PostsScreenPanel,
        title = {
            Text(
                text = "Xoa bai viet?",
                color = PostsScreenPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Text(
                text = "Bai viet \"$title\" se bi xoa khoi danh sach.",
                color = PostsScreenSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isBusy) {
                Text("Huy", color = PostsScreenSecondary)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isBusy) {
                Text(
                    text = "Xoa",
                    color = PostsScreenDanger,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
internal fun PostsErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.Spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing16))
        PrimaryPillButton(
            label = "Thu lai",
            onClick = onRetry
        )
    }
}

@Composable
internal fun PostsInlineError(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Dimens.Spacing20)
            .clip(RoundedCornerShape(Shapes.Radius24))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius24))
            .padding(Dimens.Spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
    ) {
        Text(
            text = message,
            color = PostsScreenDanger,
            style = MaterialTheme.typography.bodyMedium
        )
        PrimaryPillButton(
            label = "Thu tai bai viet",
            onClick = onRetry
        )
    }
}
