package com.example.uitvolunteermap.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Elevations
import com.example.uitvolunteermap.core.ui.theme.FontTokens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.core.ui.theme.TextSize
import com.example.uitvolunteermap.features.auth.presentation.LoginUiState

@Composable
internal fun LoginFormCard(
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing18 = Dimens.Spacing16 + Dimens.Spacing2
    val buttonHeight = Dimens.Spacing24 + Dimens.Spacing28

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Elevations.Level8,
                shape = RoundedCornerShape(Shapes.Radius24),
                ambientColor = Color(0x0D1A1918),
                spotColor = Color(0x0D1A1918),
            )
            .clip(RoundedCornerShape(Shapes.Radius24))
            .background(Color(0xFFFFFFFF))
            .border(
                width = Dimens.Spacing2 / 2,
                color = Color(0xFFE5E4E1),
                shape = RoundedCornerShape(Shapes.Radius24),
            )
            .padding(
                start = spacing18,
                top = spacing18,
                end = spacing18,
                bottom = Dimens.Spacing20,
            ),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing14),
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
        )

        LoginInputField(
            label = "Mat khau",
            value = state.password,
            placeholder = "••••••••••",
            onValueChange = onPasswordChanged,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            visualTransformation = PasswordVisualTransformation(),
            error = state.passwordError,
            fieldTag = VolunteerFlowTestTags.LoginPasswordField,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "Quen mat khau?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontTokens.Form,
                    fontSize = TextSize.Size13,
                    fontWeight = FontWeight.Medium,
                ),
                color = Color(0xFF3D8A5A),
            )
        }

        Button(
            onClick = onLoginClick,
            enabled = state.isLoginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .testTag(VolunteerFlowTestTags.LoginSubmitButton)
                .shadow(
                    elevation = Elevations.Level6,
                    shape = RoundedCornerShape(Shapes.RadiusPill),
                    ambientColor = Color(0x243D8A5A),
                    spotColor = Color(0x243D8A5A),
                ),
            shape = RoundedCornerShape(Shapes.RadiusPill),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3D8A5A),
                contentColor = Color(0xFFFFFFFF),
                disabledContainerColor = Color(0x803D8A5A),
                disabledContentColor = Color(0xFFFFFFFF),
            ),
            contentPadding = PaddingValues(
                horizontal = spacing18,
                vertical = Dimens.Spacing16,
            ),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.Spacing20),
                    strokeWidth = Dimens.Spacing2,
                    color = Color(0xFFFFFFFF),
                )
            } else {
                Text(
                    text = "Dang nhap",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontTokens.Form,
                        fontSize = TextSize.Size15,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        if (state.authError != null) {
            Text(
                text = state.authError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
