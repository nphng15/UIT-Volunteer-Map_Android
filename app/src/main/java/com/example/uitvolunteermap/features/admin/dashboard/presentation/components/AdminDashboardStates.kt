package com.example.uitvolunteermap.features.admin.dashboard.presentation.components

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

@Composable
internal fun AdminDashboardSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = AdminDashboardTokens.TextPrimary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
internal fun AdminDashboardErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AdminDashboardTokens.Border, AdminDashboardTokens.CardShape)
                .background(AdminDashboardTokens.Surface, AdminDashboardTokens.CardShape)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = AdminDashboardTokens.TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminDashboardTokens.BrandAccent,
                    contentColor = AdminDashboardTokens.TextInverse
                )
            ) {
                Text(text = "Thử lại")
            }
        }
    }
}
