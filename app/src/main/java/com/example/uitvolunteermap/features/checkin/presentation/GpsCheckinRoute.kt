package com.example.uitvolunteermap.features.checkin.presentation

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@SuppressLint("MissingPermission")
@Composable
fun GpsCheckinRoute(
    onBack: () -> Unit,
    viewModel: GpsCheckinViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.onEvent(GpsCheckinUiEvent.PermissionGranted)
        } else {
            viewModel.onEvent(GpsCheckinUiEvent.PermissionDenied)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                GpsCheckinUiEffect.NavigateBack -> onBack()

                GpsCheckinUiEffect.RequestLocationPermission -> {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                GpsCheckinUiEffect.RequestLocation -> {
                    val cancellationToken = CancellationTokenSource()
                    locationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationToken.token
                    ).addOnSuccessListener { location ->
                        if (location != null) {
                            viewModel.onEvent(
                                GpsCheckinUiEvent.LocationReceived(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        } else {
                            viewModel.onEvent(
                                GpsCheckinUiEvent.LocationFailed("Không thể lấy vị trí. Vui lòng thử lại.")
                            )
                        }
                    }.addOnFailureListener { e ->
                        viewModel.onEvent(
                            GpsCheckinUiEvent.LocationFailed(
                                e.localizedMessage ?: "Lỗi khi lấy vị trí GPS."
                            )
                        )
                    }
                }

                is GpsCheckinUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    GpsCheckinScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
