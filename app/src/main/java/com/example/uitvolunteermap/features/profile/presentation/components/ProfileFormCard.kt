package com.example.uitvolunteermap.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import com.example.uitvolunteermap.R
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.Shapes

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
    fullNameError: String? = null,
    emailError: String? = null,
    phoneError: String? = null,
    isReadOnly: Boolean = false,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // SỬA: Dùng Shapes Token thay cho 24.dp
        shape = RoundedCornerShape(Shapes.Radius24),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Spacing20), // SỬA: Dùng Dimens Token
            // SỬA: Dùng Spacing16 Token thay cho 16.dp
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
        ) {
            Text(
                // SỬA: Dùng stringResource thay cho text cứng
                text = stringResource(id = R.string.profile_personal_info_title),
                color = ColorTokens.TextSecondary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontTokens.Body, // SỬA: Dùng FontTokens.Body thay cho DMSans cục bộ
                ),
            )

            ProfileTextField(
                label = stringResource(id = R.string.label_full_name),
                value = fullName,
                onValueChange = onFullNameChanged,
                error = fullNameError,
                readOnly = isReadOnly,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                // SỬA: Dùng Spacing12 Token
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
            ) {
                ProfileTextField(
                    label = stringResource(id = R.string.label_mssv),
                    value = mssv,
                    onValueChange = {}, // MSSV thường không cho sửa
                    readOnly = true, // Force readOnly cho MSSV theo logic Backend
                    modifier = Modifier.weight(1f)
                )
                ProfileTextField(
                    label = stringResource(id = R.string.label_class),
                    value = className,
                    onValueChange = onClassNameChanged,
                    readOnly = isReadOnly,
                    modifier = Modifier.weight(1f)
                )
            }

            ProfileTextField(
                label = stringResource(id = R.string.label_email),
                value = email,
                onValueChange = onEmailChanged,
                error = emailError,
                readOnly = isReadOnly,
            )

            ProfileTextField(
                label = stringResource(id = R.string.label_phone),
                value = phoneNumber,
                onValueChange = onPhoneNumberChanged,
                error = phoneError,
                readOnly = isReadOnly,
            )
        }
    }
}