package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
            .clip(RoundedCornerShape(Shapes.Radius28))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius28))
            .padding(Dimens.Spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Shapes.RadiusPill))
                .background(PostsScreenAccentSoft)
                .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing6)
        ) {
            Text(
                text = "Chưa có dữ liệu",
                color = PostsScreenAccent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = if (selectedTeamName == null) {
                "Chưa có bài viết nào cho chiến dịch này."
            } else {
                "Chưa có bài viết nào cho $selectedTeamName."
            },
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (canCreatePost) {
                "Trưởng nhóm có thể tạo bài viết đầu tiên bằng nút cộng ở header."
            } else {
                "Tài khoản khách chỉ có quyền xem bài viết."
            },
            color = PostsScreenSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        if (canCreatePost) {
            PrimaryPillButton(
                label = "Tạo bài viết",
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
        tonalElevation = 0.dp,
        title = {
            Text(
                text = "Xóa bài viết?",
                color = PostsScreenPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Text(
                text = "Bài viết \"$title\" sẽ bị xóa khỏi danh sách.",
                color = PostsScreenSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isBusy) {
                Text("Hủy", color = PostsScreenSecondary)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isBusy) {
                Text(
                    text = "Xóa",
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
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Shapes.RadiusPill))
                .background(PostsScreenDangerSoft)
                .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing6)
        ) {
            Text(
                text = "Lỗi tải dữ liệu",
                color = PostsScreenDanger,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(Dimens.Spacing12))
        Text(
            text = message,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing16))
        PrimaryPillButton(
            label = "Thử lại",
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
            .clip(RoundedCornerShape(Shapes.Radius28))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius28))
            .padding(Dimens.Spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Shapes.RadiusPill))
                .background(PostsScreenDangerSoft)
                .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing6)
        ) {
            Text(
                text = "Cảnh báo",
                color = PostsScreenDanger,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = message,
            color = PostsScreenDanger,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        PrimaryPillButton(
            label = "Tải lại bài viết",
            onClick = onRetry
        )
    }
}
