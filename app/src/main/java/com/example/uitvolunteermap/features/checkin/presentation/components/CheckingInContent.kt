package com.example.uitvolunteermap.features.checkin.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiState
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.PrimaryOrange
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextSecondary

@Composable
internal fun CheckingInContent(state: GpsCheckinUiState) {
    Spacer(modifier = Modifier.height(60.dp))
    CircularProgressIndicator(
        modifier = Modifier.size(56.dp),
        color = PrimaryOrange,
        strokeWidth = 4.dp
    )
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "Đang xác minh vị trí...",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.selectedCampaign?.campaignName ?: "",
        fontSize = 14.sp,
        color = TextSecondary
    )
}
