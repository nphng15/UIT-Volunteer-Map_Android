package com.example.uitvolunteermap.features.checkin.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiEvent
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiState
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.PrimaryOrange
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.SuccessGreen
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextSecondary

@Composable
internal fun SuccessContent(
    state: GpsCheckinUiState,
    onEvent: (GpsCheckinUiEvent) -> Unit
) {
    Spacer(modifier = Modifier.height(40.dp))

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(SuccessGreen.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = SuccessGreen
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "Điểm danh thành công!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.selectedCampaign?.campaignName ?: "",
        fontSize = 14.sp,
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            state.checkinDistance?.let { distance ->
                Row {
                    Text("Khoảng cách:", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${String.format("%.1f", distance)}m",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            state.checkinTime?.let { time ->
                Row {
                    Text("Thời gian:", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        time,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = { onEvent(GpsCheckinUiEvent.BackClicked) },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text("Quay lại", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
internal fun FailedContent(
    state: GpsCheckinUiState,
    onEvent: (GpsCheckinUiEvent) -> Unit
) {
    Spacer(modifier = Modifier.height(40.dp))

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(PrimaryOrange.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = PrimaryOrange
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "Điểm danh thất bại",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.errorMessage ?: "Đã xảy ra lỗi. Vui lòng thử lại.",
        fontSize = 14.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onEvent(GpsCheckinUiEvent.RetryClicked) },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
    ) {
        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Thử lại", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
        onClick = { onEvent(GpsCheckinUiEvent.BackClicked) },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text("Quay lại", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
