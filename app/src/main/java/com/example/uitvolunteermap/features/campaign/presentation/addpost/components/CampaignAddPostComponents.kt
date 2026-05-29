package com.example.uitvolunteermap.features.campaign.presentation.addpost.components

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupInput
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupLabel
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupPlaceholder
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSecondary
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupSheetStroke
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupText
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupUpload
import com.example.uitvolunteermap.features.campaign.presentation.addpost.components.CampaignAddPostTokens.PopupUploadStroke

@Composable
internal fun PopupField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = PopupLabel,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
internal fun PopupTextInput(
    value: String,
    placeholder: String,
    minHeight: Dp,
    singleLine: Boolean,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = PopupText,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minHeight)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PopupInput)
                    .border(
                        width = 1.dp,
                        color = PopupSheetStroke,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = PopupPlaceholder,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
internal fun AttachmentChip(
    name: String,
    enabled: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PopupUpload)
            .border(1.dp, PopupUploadStroke, RoundedCornerShape(999.dp))
            .clickable(enabled = enabled, onClick = onRemove)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = PopupLabel,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "x",
            color = PopupSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
