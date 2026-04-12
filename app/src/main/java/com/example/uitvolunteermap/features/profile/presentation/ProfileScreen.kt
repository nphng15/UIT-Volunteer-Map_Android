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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.profile.domain.entity.UserRole
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
    onConfirmSave: () -> Unit,
    onDismissDialog: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val isReadOnly = state.role == UserRole.STUDENT

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
                ProfileHeaderCard(
                    fullName = state.fullName,
                    role = state.role,
                )

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
                    fullNameError = state.fullNameError,
                    emailError = state.emailError,
                    phoneError = state.phoneError,
                    isReadOnly = isReadOnly,
                )

                if (state.emailConflictError != null) {
                    Text(
                        text = state.emailConflictError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = onSaveClick,
                    enabled = state.isDirty && state.isSaveEnabled,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D5C3A),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF1D5C3A).copy(alpha = 0.6f),
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

                Button(
                    onClick = onLogoutClick,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB00020),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Đăng xuất")
                }
            }

            if (state.showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = onDismissDialog,
                    title = { Text(text = "Xác nhận lưu thay đổi") },
                    text = { Text(text = "Bạn có muốn lưu thay đổi hồ sơ không?") },
                    confirmButton = {
                        TextButton(onClick = onConfirmSave) {
                            Text(text = "Lưu")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismissDialog) {
                            Text(text = "Hủy")
                        }
                    }
                )
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
                role = UserRole.STUDENT,
                isLoading = false,
                isSaving = false,
                saveSuccess = false,
                isDirty = true,
                showConfirmDialog = false,
                emailConflictError = null,
                originalFullName = "Nguyễn Văn A",
                originalEmail = "student@gm.uit.edu.vn",
                originalPhone = "0912345678",
                fullNameError = null,
                emailError = null,
                phoneError = null,
            ),
            onBackClick = {},
            onFullNameChanged = {},
            onClassNameChanged = {},
            onEmailChanged = {},
            onPhoneNumberChanged = {},
            onSaveClick = {},
            onConfirmSave = {},
            onDismissDialog = {},
            onLogoutClick = {},
        )
    }
}

