package com.example.uitvolunteermap.features.checkin.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiState
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.AccentBlue
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.PrimaryOrange
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextSecondary

@Composable
internal fun IdleContent(state: GpsCheckinUiState) {
    Spacer(modifier = Modifier.height(40.dp))
    Icon(
        imageVector = Icons.Default.MyLocation,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = AccentBlue
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Chờ cấp quyền vị trí",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.errorMessage ?: "Vui lòng cấp quyền truy cập vị trí để điểm danh.",
        fontSize = 14.sp,
        color = if (state.errorMessage != null) PrimaryOrange else TextSecondary,
        textAlign = TextAlign.Center
    )
}
