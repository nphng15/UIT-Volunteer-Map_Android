package com.example.uitvolunteermap.features.profile.presentation

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
import androidx.compose.ui.res.stringResource // Thêm import này
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.R // Import R của dự án
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.UserRole
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.Shapes // Sử dụng Shapes token
import com.example.uitvolunteermap.core.ui.theme.UITVolunteerTheme
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

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ProfileTopAppBar(onBackClick = onBackClick)
            },
            containerColor = ColorTokens.BrandSurface
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(scrollState)
                    .padding(Dimens.Spacing24), // Sử dụng Dimens token
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
                    isReadOnly = state.isReadOnly,
                )

                if (state.emailConflictError != null) {
                    Text(
                        text = state.emailConflictError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Nút Lưu thay đổi
                Button(
                    onClick = onSaveClick,
                    enabled = state.canEdit && state.isDirty && state.isSaveEnabled,
                    shape = RoundedCornerShape(Shapes.RadiusCircle), // Sử dụng Shapes token
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorTokens.MapMarker,
                        contentColor = ColorTokens.TextInverse,
                        disabledContainerColor = ColorTokens.MapMarker.copy(alpha = 0.6f),
                        disabledContentColor = ColorTokens.TextInverse.copy(alpha = 0.6f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = ColorTokens.TextInverse,
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
                                tint = ColorTokens.TextInverse,
                                modifier = Modifier.height(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Dimens.Spacing8))
                            // Sử dụng stringResource thay cho text cứng
                            Text(text = stringResource(id = R.string.label_save_changes))
                        }
                    }
                }

                // Nút Đăng xuất
                Button(
                    onClick = onLogoutClick,
                    enabled = state.canLogout,
                    shape = RoundedCornerShape(Shapes.RadiusCircle),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorTokens.BrandAccent,
                        contentColor = ColorTokens.TextInverse,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = stringResource(id = R.string.label_logout))
                }
            }

            // Dialog xác nhận
            if (state.showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = onDismissDialog,
                    title = { Text(text = stringResource(id = R.string.dialog_confirm_save_title)) },
                    text = { Text(text = stringResource(id = R.string.dialog_confirm_save_message)) },
                    confirmButton = {
                        TextButton(onClick = onConfirmSave) {
                            Text(text = stringResource(id = R.string.action_save))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismissDialog) {
                            Text(text = stringResource(id = R.string.action_cancel))
                        }
                    }
                )
            }

            // Loading Overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorTokens.BrandPrimary)
                }
            }
        }
    }
}