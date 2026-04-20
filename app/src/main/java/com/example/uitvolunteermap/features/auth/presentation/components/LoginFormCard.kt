package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.auth.presentation.LoginUiState

@Composable
internal fun LoginFormCard(
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = LoginPalette.Primary.copy(alpha = 0.08f),
                spotColor = LoginPalette.Primary.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(24.dp))
            .background(LoginPalette.Surface)
            .border(
                width = 1.dp,
                color = LoginPalette.Border,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(
                horizontal = Dimens.Spacing16,
                vertical = Dimens.Spacing16 + Dimens.Spacing2,
            ),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
    ) {
        LoginInputField(
            label = "Email",
            value = state.email,
            placeholder = "volunteer@uit.edu.vn",
            onValueChange = onEmailChanged,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            visualTransformation = VisualTransformation.None,
            error = state.emailError,
            fieldTag = VolunteerFlowTestTags.LoginEmailField,
            enabled = !state.isLoading,
        )

        LoginInputField(
            label = "Mật khẩu",
            value = state.password,
            placeholder = "••••••••",
            onValueChange = onPasswordChanged,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (state.isLoginEnabled) {
                        onLoginClick()
                    }
                }
            ),
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingLabel = if (isPasswordVisible) "Ẩn" else "Hiện",
            onTrailingLabelClick = { isPasswordVisible = !isPasswordVisible },
            error = state.passwordError,
            fieldTag = VolunteerFlowTestTags.LoginPasswordField,
            enabled = !state.isLoading,
        )

        if (state.authError != null) {
            Text(
                text = state.authError,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = LoginPalette.Error,
            )
        }

        Button(
            onClick = onLoginClick,
            enabled = state.isLoginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag(VolunteerFlowTestTags.LoginSubmitButton)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = LoginPalette.Accent.copy(alpha = 0.28f),
                    spotColor = LoginPalette.Accent.copy(alpha = 0.28f),
                ),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoginPalette.Accent,
                contentColor = LoginPalette.TextInverse,
                disabledContainerColor = LoginPalette.Accent.copy(alpha = 0.45f),
                disabledContentColor = LoginPalette.TextInverse.copy(alpha = 0.9f),
            ),
            contentPadding = PaddingValues(horizontal = Dimens.Spacing16),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = LoginPalette.TextInverse,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                ) {
                    Text(
                        text = "Vào hệ thống",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.sp,
                        ),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Quên mật khẩu?",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = LoginPalette.TextSecondary,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Text(
                text = "Đăng ký",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = LoginPalette.Primary,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}
