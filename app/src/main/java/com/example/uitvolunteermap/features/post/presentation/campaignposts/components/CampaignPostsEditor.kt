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
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Dimens.Spacing20)
            .clip(RoundedCornerShape(Shapes.Radius24))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius24))
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
                        "Tao bai viet moi"
                    } else {
                        "Cap nhat bai viet"
                    },
                    color = PostsScreenPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Dien thong tin ngan gon theo bo cuc post trong bang tin.",
                    color = PostsScreenSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            CircleIconButton(
                icon = Icons.Rounded.Close,
                contentDescription = "Dong",
                onClick = onDismiss
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
            Text(
                text = "Doi phu trach",
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
            label = "Tieu de bai viet",
            value = editor.title,
            placeholder = "Nhap tieu de ro rang cho bai post",
            minHeight = 56.dp,
            onValueChange = onTitleChanged
        )

        EditorField(
            label = "Noi dung",
            value = editor.content,
            placeholder = "Mo ta nhanh ket qua, hoat dong noi bat, thong tin can truyen thong",
            minHeight = 110.dp,
            onValueChange = onContentChanged
        )

        if (editor.mode == CampaignPostEditorMode.Create) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
                Text(
                    text = "Anh dinh kem",
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
                            placeholder = "Vi du: post_day_1.jpg",
                            minHeight = 56.dp,
                            onValueChange = onAttachmentInputChanged
                        )
                    }
                    PrimaryPillButton(
                        label = "Them",
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
                label = "Dong",
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            PrimaryPillButton(
                label = if (isSaving) "Dang luu..." else "Luu bai viet",
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
                        .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius18))
                        .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing12),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = PostsScreenSecondary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
