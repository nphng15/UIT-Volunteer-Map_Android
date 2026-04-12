package com.example.uitvolunteermap.features.auth.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.auth.presentation.LoginUiState
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
    modifier: Modifier = Modifier,
) {
    LoginScreenContainer(modifier = modifier) {
        Spacer(modifier = Modifier.height(Dimens.Spacing24))

        LoginHeroHeader()

        Spacer(modifier = Modifier.height(Dimens.Spacing24))

        LoginFormCard(
            state = state,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onLoginClick = onLoginClick,
        )

        Spacer(modifier = Modifier.height(Dimens.Spacing24))

        LoginFooter()
    }
}
