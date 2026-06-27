package com.example.uitvolunteermap.features.campaign.presentation.areamap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@SuppressLint("MissingPermission")
@Composable
fun CampaignAreaMapRoute(
    onBack: () -> Unit,
    viewModel: CampaignAreaMapViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    fun fetchLocation() {
        if (!hasLocationPermission()) {
            viewModel.onEvent(CampaignAreaMapUiEvent.LocationUnavailable)
            return
        }
        fun fallbackToLast() {
            locationClient.lastLocation
                .addOnSuccessListener { last ->
                    if (last != null) {
                        viewModel.onEvent(CampaignAreaMapUiEvent.LocationReceived(last.latitude, last.longitude))
                    } else {
                        viewModel.onEvent(CampaignAreaMapUiEvent.LocationUnavailable)
                    }
                }
                .addOnFailureListener { viewModel.onEvent(CampaignAreaMapUiEvent.LocationUnavailable) }
        }
        val token = CancellationTokenSource()
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.onEvent(CampaignAreaMapUiEvent.LocationReceived(location.latitude, location.longitude))
                } else {
                    fallbackToLast()
                }
            }
            .addOnFailureListener { fallbackToLast() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) fetchLocation()
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                CampaignAreaMapUiEffect.NavigateBack -> onBack()
                CampaignAreaMapUiEffect.RequestLocation -> {
                    if (hasLocationPermission()) {
                        fetchLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
                is CampaignAreaMapUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CampaignAreaMapScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
