package com.example.uitvolunteermap.features.checkin.presentation.hub

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.features.checkin.presentation.hub.camera.CheckinCameraController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun CheckinHubRoute(
    onTabSelected: (VolunteerBottomBarTab) -> Unit,
    viewModel: CheckinHubViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val cameraController = remember { CheckinCameraController(context) }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun fetchLocation() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return
        val token = CancellationTokenSource()
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.onEvent(
                        CheckinHubUiEvent.LocationReceived(location.latitude, location.longitude)
                    )
                }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            viewModel.onEvent(CheckinHubUiEvent.PermissionGranted)
        } else {
            viewModel.onEvent(CheckinHubUiEvent.PermissionDenied)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                CheckinHubUiEffect.RequestLocationPermission -> {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA
                        )
                    )
                }
                CheckinHubUiEffect.RequestLocation -> fetchLocation()
                is CheckinHubUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CheckinHubScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onTabSelected = onTabSelected,
        viewfinder = { modifier ->
            AndroidView(
                modifier = modifier,
                factory = { ctx ->
                    PreviewView(ctx).also { previewView ->
                        cameraController.bindToLifecycle(lifecycleOwner, previewView)
                    }
                }
            )
        },
        onShutterClick = {
            scope.launch {
                runCatching { cameraController.capture() }
                    .onSuccess { file -> viewModel.onEvent(CheckinHubUiEvent.PhotoCaptured(file)) }
                    .onFailure {
                        snackbarHostState.showSnackbar("Không chụp được ảnh, vui lòng thử lại.")
                    }
            }
        }
    )
}
