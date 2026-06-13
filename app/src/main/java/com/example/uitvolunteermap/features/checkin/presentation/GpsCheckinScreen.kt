package com.example.uitvolunteermap.features.checkin.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.features.checkin.presentation.components.CheckingInContent
import com.example.uitvolunteermap.features.checkin.presentation.components.FailedContent
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.AccentBlue
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextTertiary
import com.example.uitvolunteermap.features.checkin.presentation.components.HistorySection
import com.example.uitvolunteermap.features.checkin.presentation.components.IdleContent
import com.example.uitvolunteermap.features.checkin.presentation.components.LoadingLocationContent
import com.example.uitvolunteermap.features.checkin.presentation.components.LocationReadyContent
import com.example.uitvolunteermap.features.checkin.presentation.components.SuccessContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsCheckinScreen(
    state: GpsCheckinUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (GpsCheckinUiEvent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ĐIỂM DANH GPS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = TextTertiary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(GpsCheckinUiEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(GpsCheckinUiEvent.ToggleHistory) }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Lịch sử điểm danh",
                            tint = if (state.isHistoryVisible) AccentBlue else TextTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFF8FCFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.screenState) {
                CheckinScreenState.Idle -> IdleContent(state)
                CheckinScreenState.LoadingLocation -> LoadingLocationContent()
                CheckinScreenState.LocationReady -> LocationReadyContent(state, onEvent)
                CheckinScreenState.CheckingIn -> CheckingInContent(state)
                CheckinScreenState.Success -> SuccessContent(state, onEvent)
                CheckinScreenState.Failed -> FailedContent(state, onEvent)
            }

            AnimatedVisibility(
                visible = state.isHistoryVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                HistorySection(state)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
