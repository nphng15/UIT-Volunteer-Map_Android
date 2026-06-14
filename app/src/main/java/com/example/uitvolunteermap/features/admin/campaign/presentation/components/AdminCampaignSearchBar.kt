package com.example.uitvolunteermap.features.admin.campaign.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AdminCampaignSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = "Tìm kiếm chiến dịch...", color = AdminCampaignTokens.MutedText) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = AdminCampaignTokens.MutedText
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AdminCampaignTokens.Surface,
            unfocusedContainerColor = AdminCampaignTokens.Surface,
            focusedBorderColor = AdminCampaignTokens.Accent,
            unfocusedBorderColor = AdminCampaignTokens.Border,
            focusedTextColor = AdminCampaignTokens.PrimaryText,
            unfocusedTextColor = AdminCampaignTokens.PrimaryText
        )
    )
}
