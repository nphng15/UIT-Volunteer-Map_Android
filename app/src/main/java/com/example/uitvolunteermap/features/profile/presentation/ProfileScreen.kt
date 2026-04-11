package com.example.uitvolunteermap.features.profile.presentation

import com.example.uitvolunteermap.core.ui.theme.UITVolunteerTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.profile.presentation.components.ProfileFormCard
import com.example.uitvolunteermap.features.profile.presentation.components.ProfileHeaderCard
import com.example.uitvolunteermap.features.profile.presentation.components.ProfileTopAppBar

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onBackClick: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onClassNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ProfileTopAppBar(onBackClick = onBackClick)
            },
            containerColor = Color(0xFFF2F2F2)
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(scrollState)
                    .padding(Dimens.Spacing24),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing24)
            ) {
                ProfileHeaderCard(fullName = state.fullName)

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ProfileFormCard(
                    fullName = state.fullName,
                    mssv = state.mssv,
                    className = state.className,
                    email = state.email,
                    phoneNumber = state.phoneNumber,
                    onFullNameChanged = onFullNameChanged,
                    onClassNameChanged = onClassNameChanged,
                    onEmailChanged = onEmailChanged,
                    onPhoneNumberChanged = onPhoneNumberChanged,
                )

                Button(
                    onClick = onSaveClick,
                    enabled = state.isSaveEnabled,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.6f),
                        disabledContentColor = Color.White.copy(alpha = 0.6f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.height(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Lưu thay đổi")
                        }
                    }
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    UITVolunteerTheme {
        ProfileScreen(
            state = ProfileUiState(
                userId = "15",
                fullName = "Nguyễn Văn A",
                mssv = "23520000",
                className = "IS2023",
                email = "student@gm.uit.edu.vn",
                phoneNumber = "0912345678",
                createdAt = "2026-03-01 09:30:00",
                isLoading = false,
                isSaving = false,
                errorMessage = null,
                saveSuccess = false,
            ),
            onBackClick = {},
            onFullNameChanged = {},
            onClassNameChanged = {},
            onEmailChanged = {},
            onPhoneNumberChanged = {},
            onSaveClick = {},
        )
    }
}
