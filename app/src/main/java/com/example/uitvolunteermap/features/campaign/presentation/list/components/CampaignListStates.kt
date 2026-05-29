package com.example.uitvolunteermap.features.campaign.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListAccentSoft
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListEmptyIconBackground
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListSecondaryText

@Composable
internal fun CampaignListEmptyState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = ListEmptyIconBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "—",
                color = ListSecondaryText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có chiến dịch nào",
            color = ListPrimaryText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Kéo xuống để làm mới hoặc quay lại sau.",
            color = ListSecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRefresh,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ListAccentSoft,
                contentColor = ListPrimaryText
            )
        ) {
            Text(text = "Làm mới", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun CampaignListErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = ListPrimaryText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ListAccentSoft,
                contentColor = ListPrimaryText
            )
        ) {
            Text(text = "Thử lại", fontWeight = FontWeight.Bold)
        }
    }
}
