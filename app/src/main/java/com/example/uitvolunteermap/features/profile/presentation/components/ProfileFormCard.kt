package com.example.uitvolunteermap.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens

private val DMSans = FontFamily.SansSerif

@Composable
fun ProfileFormCard(
    fullName: String,
    mssv: String,
    className: String,
    email: String,
    phoneNumber: String,
    onFullNameChanged: (String) -> Unit,
    onClassNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Thông tin cá nhân",
                color = Color(0xFF6D6C6A),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = DMSans,
                ),
            )

            ProfileTextField(
                label = "Họ và tên",
                value = fullName,
                onValueChange = onFullNameChanged,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),// Thêm padding cho đẹp nếu cần
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileTextField(
                    label = "MSSV",
                    value = mssv,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.weight(1f) // Chiếm 50%
                )
                ProfileTextField(
                    label = "Lớp",
                    value = className,
                    onValueChange = onClassNameChanged,
                    modifier = Modifier.weight(1f) // Chiếm 50%
                )
            }

            ProfileTextField(
                label = "Email",
                value = email,
                onValueChange = onEmailChanged,
            )

            ProfileTextField(
                label = "SĐT",
                value = phoneNumber,
                onValueChange = onPhoneNumberChanged,
            )
        }
    }
}
