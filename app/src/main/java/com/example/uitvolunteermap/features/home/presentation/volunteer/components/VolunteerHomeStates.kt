package com.example.uitvolunteermap.features.home.presentation.volunteer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.CardShape
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenAccent
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBorder
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSurface
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextInverse
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextSecondary

@Composable
internal fun EmptyVolunteerCampaignState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, ScreenBorder, CardShape)
            .background(ScreenSurface, CardShape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chưa có chiến dịch để hiển thị.",
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Thử tải lại để đồng bộ dữ liệu mới nhất.",
            color = ScreenTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ScreenPrimary,
                contentColor = ScreenTextInverse
            )
        ) {
            Text(text = "Thử lại")
        }
    }
}

@Composable
internal fun VolunteerHomeErrorState(
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ScreenBorder, CardShape)
                .background(ScreenSurface, CardShape)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScreenAccent,
                    contentColor = ScreenTextInverse
                )
            ) {
                Text(text = "Thử lại")
            }
        }
    }
}
