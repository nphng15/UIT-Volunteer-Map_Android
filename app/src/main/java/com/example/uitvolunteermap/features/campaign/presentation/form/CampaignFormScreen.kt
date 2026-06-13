package com.example.uitvolunteermap.features.campaign.presentation.form

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormContentBackground
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.form.components.CampaignFormTokens.FormTopBackground
import com.example.uitvolunteermap.features.campaign.presentation.form.components.DiscardChangesDialog
import com.example.uitvolunteermap.features.campaign.presentation.form.components.FormBody
import com.example.uitvolunteermap.features.campaign.presentation.form.components.PreloadErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignFormScreen(
    state: CampaignFormUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignFormUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = state.isDirty && !state.showDiscardDialog) {
        onEvent(CampaignFormUiEvent.BackClicked)
    }

    if (state.showDiscardDialog) {
        DiscardChangesDialog(
            onConfirm = { onEvent(CampaignFormUiEvent.DiscardConfirmed) },
            onDismiss = { onEvent(CampaignFormUiEvent.DiscardCancelled) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = FormContentBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state.mode) {
                            CampaignFormMode.Create -> "Tạo chiến dịch"
                            CampaignFormMode.Edit -> "Chỉnh sửa chiến dịch"
                        },
                        color = FormPrimaryText,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CampaignFormUiEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = FormPrimaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FormTopBackground)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoadingPreload -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.campaignName.isEmpty() && state.mode == CampaignFormMode.Edit -> {
                    PreloadErrorState(
                        message = state.errorMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    FormBody(state = state, onEvent = onEvent)
                }
            }
        }
    }
}
