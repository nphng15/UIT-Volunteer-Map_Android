package com.example.uitvolunteermap.features.campaign.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.list.CampaignListItemUiModel
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListBorder
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListContentBackground
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListDeleteIconTint
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListSecondaryText

@Composable
internal fun CampaignListItem(
    campaign: CampaignListItemUiModel,
    showDeleteButton: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(width = 1.dp, color = ListBorder, shape = RoundedCornerShape(20.dp))
            .background(color = ListContentBackground, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(
                start = 16.dp,
                top = 16.dp,
                bottom = 16.dp,
                end = if (showDeleteButton) 4.dp else 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = campaign.campaignName,
                color = ListPrimaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = campaign.dateRange,
                color = ListSecondaryText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (campaign.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = campaign.description,
                    color = ListSecondaryText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (showDeleteButton) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Xóa chiến dịch",
                    tint = ListDeleteIconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
