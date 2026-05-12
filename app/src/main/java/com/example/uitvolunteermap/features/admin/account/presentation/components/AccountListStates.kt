package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun AccountEmptyState(message: String, onRefresh: () -> Unit) {
    StateColumn {
        Text(
            text = "Không có tài khoản nào",
            color = AccountTokens.PrimaryText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            color = AccountTokens.SecondaryText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        RefreshButton(onRefresh)
    }
}

@Composable
internal fun AccountErrorState(message: String, onRetry: () -> Unit) {
    StateColumn {
        Text(
            text = message,
            color = AccountTokens.PrimaryText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        RefreshButton(onRetry, label = "Thử lại")
    }
}

@Composable
private fun StateColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
private fun RefreshButton(onClick: () -> Unit, label: String = "Làm mới") {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccountTokens.Accent,
            contentColor = AccountTokens.Surface
        )
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
    }
}
