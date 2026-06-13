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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiState
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.SuccessGreen
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextSecondary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextTertiary

@Composable
internal fun HistorySection(state: GpsCheckinUiState) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        Text(
            text = "LỊCH SỬ ĐIỂM DANH",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextTertiary
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (state.isHistoryLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        } else if (state.history.isEmpty()) {
            Text(
                text = "Chưa có lịch sử điểm danh.",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            state.history.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.campaignName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${item.checkedInAt} · ${String.format("%.0f", item.distance)}m",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
