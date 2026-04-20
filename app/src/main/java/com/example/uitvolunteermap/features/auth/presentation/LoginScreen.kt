package com.example.uitvolunteermap.features.auth.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.app.UITVolunteerTheme
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.auth.presentation.components.LoginFooter
import com.example.uitvolunteermap.features.auth.presentation.components.LoginFormCard
import com.example.uitvolunteermap.features.auth.presentation.components.LoginHeroHeader
import com.example.uitvolunteermap.features.auth.presentation.components.LoginScreenContainer

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onContinueAsGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoginScreenContainer(modifier = modifier.testTag(VolunteerFlowTestTags.LoginScreen)) {
        Spacer(modifier = Modifier.height(Dimens.Spacing24))

        LoginHeroHeader()

        Spacer(modifier = Modifier.height(Dimens.Spacing32))

        LoginFormCard(
            state = state,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onLoginClick = onLoginClick,
        )

        Spacer(modifier = Modifier.height(Dimens.Spacing20))

        LoginFooter(onContinueAsGuestClick = onContinueAsGuestClick)

        Spacer(modifier = Modifier.height(Dimens.Spacing28))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    UITVolunteerTheme(darkTheme = false) {
        LoginScreen(
            state = LoginUiState(
                email = "volunteer@uit.edu.vn",
                password = "volunteer123",
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onContinueAsGuestClick = {},
        )
    }
}
