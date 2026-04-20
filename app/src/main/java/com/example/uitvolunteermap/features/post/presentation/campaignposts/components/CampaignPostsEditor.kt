package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostEditorMode
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostEditorUiModel
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsTeamUiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PostEditorCard(
    editor: CampaignPostEditorUiModel,
    teams: List<CampaignPostsTeamUiModel>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onTeamSelected: (Int) -> Unit,
    onAttachmentInputChanged: (String) -> Unit,
    onAttachmentAdded: () -> Unit,
    onAttachmentRemoved: (Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.Radius28))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius28))
            .padding(Dimens.Spacing14),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing2)) {
                Text(
                    text = if (editor.mode == CampaignPostEditorMode.Create) {
                        "Tạo bài viết"
                    } else {
                        "Chỉnh sửa bài viết"
                    },
                    color = PostsScreenPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            CircleIconButton(
                icon = Icons.Rounded.Close,
                contentDescription = "Đóng",
                onClick = onDismiss
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
            Text(
                text = "Đội phụ trách",
                color = PostsScreenPrimary,
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
            ) {
                teams.forEach { team ->
                    FilterChip(
                        label = team.label,
                        count = team.postCount,
                        selected = editor.selectedTeamId == team.id,
                        onClick = { onTeamSelected(team.id) }
                    )
                }
            }
        }

        EditorField(
            label = "Tiêu đề bài viết",
            value = editor.title,
            placeholder = "Nhập tiêu đề rõ ràng cho bài viết",
            minHeight = 56.dp,
            onValueChange = onTitleChanged
        )

        EditorField(
            label = "Nội dung",
            value = editor.content,
            placeholder = "Mô tả nhanh kết quả, hoạt động nổi bật và thông tin cần truyền thông",
            minHeight = 110.dp,
            onValueChange = onContentChanged
        )

        if (editor.mode == CampaignPostEditorMode.Create) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(Shapes.Radius22))
                    .background(PostsScreenAccentSoft)
                    .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius22))
                    .padding(Dimens.Spacing12),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
            ) {
                Text(
                    text = "Ảnh đính kèm",
                    color = PostsScreenPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        EditorField(
                            label = "",
                            value = editor.attachmentInput,
                            placeholder = "Ví dụ: post_ngay_1.jpg",
                            minHeight = 56.dp,
                            onValueChange = onAttachmentInputChanged
                        )
                    }
                    PrimaryPillButton(
                        label = "Thêm",
                        enabled = !isSaving,
                        onClick = onAttachmentAdded
                    )
                }
            }
        }

        if (editor.attachmentNames.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
            ) {
                editor.attachmentNames.forEachIndexed { index, attachmentName ->
                    AttachmentChip(
                        name = attachmentName,
                        removable = editor.mode == CampaignPostEditorMode.Create,
                        onRemove = { onAttachmentRemoved(index) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
        ) {
            SecondaryPillButton(
                label = "Đóng",
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            PrimaryPillButton(
                label = if (isSaving) "Đang lưu..." else "Lưu bài viết",
                modifier = Modifier.weight(1f),
                enabled = !isSaving,
                onClick = onSave
            )
        }
    }
}

@Composable
private fun EditorField(
    label: String,
    value: String,
    placeholder: String,
    minHeight: Dp,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                color = PostsScreenPrimary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = PostsScreenPrimary),
            cursorBrush = SolidColor(PostsScreenAccent),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(minHeight)
                        .clip(RoundedCornerShape(Shapes.Radius18))
                        .background(PostsScreenSurface)
                        .border(
                            1.dp,
                            PostsScreenAccent.copy(alpha = 0.14f),
                            RoundedCornerShape(Shapes.Radius18)
                        )
                        .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing12),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = PostsScreenSecondary.copy(alpha = 0.82f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
