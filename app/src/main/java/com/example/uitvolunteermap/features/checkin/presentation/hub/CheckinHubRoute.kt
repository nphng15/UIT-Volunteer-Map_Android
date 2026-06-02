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
        if (!granted) {
            viewModel.onEvent(CheckinHubUiEvent.LocationUnavailable)
            return
        }

        fun fallbackToLastLocation() {
            locationClient.lastLocation
                .addOnSuccessListener { last ->
                    if (last != null) {
                        viewModel.onEvent(
                            CheckinHubUiEvent.LocationReceived(last.latitude, last.longitude)
                        )
                    } else {
                        viewModel.onEvent(CheckinHubUiEvent.LocationUnavailable)
                    }
                }
                .addOnFailureListener { viewModel.onEvent(CheckinHubUiEvent.LocationUnavailable) }
        }

        val token = CancellationTokenSource()
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.onEvent(
                        CheckinHubUiEvent.LocationReceived(location.latitude, location.longitude)
                    )
                } else {
                    fallbackToLastLocation()
                }
            }
            .addOnFailureListener { fallbackToLastLocation() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        viewModel.onEvent(CheckinHubUiEvent.CameraPermissionResult(cameraGranted))
        if (locationGranted) {
            viewModel.onEvent(CheckinHubUiEvent.PermissionGranted)
        } else {
            viewModel.onEvent(CheckinHubUiEvent.PermissionDenied)
        }
    }

    LaunchedEffect(Unit) {
        // Xin quyền một lần khi vào màn — đặt ở đây (không phải VM init) để launcher
        // chắc chắn đã sẵn sàng, tránh mất effect qua SharedFlow replay=0.
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            )
        )
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

    // Đổi camera khi state.cameraFacing thay đổi.
    LaunchedEffect(state.value.cameraFacing, state.value.cameraPermissionGranted) {
        if (state.value.cameraPermissionGranted) {
            val f = if (state.value.cameraFacing == CameraFacing.Front) {
                CheckinCameraController.Facing.Front
            } else {
                CheckinCameraController.Facing.Back
            }
            cameraController.setFacing(f)
        }
    }

    CheckinHubScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onTabSelected = onTabSelected,
        viewfinder = { modifier ->
            // Chỉ bind camera SAU khi quyền camera được cấp; AndroidView bind lần đầu trong factory.
            if (state.value.cameraPermissionGranted) {
                AndroidView(
                    modifier = modifier,
                    factory = { ctx ->
                        PreviewView(ctx).also { previewView ->
                            val f = if (state.value.cameraFacing == CameraFacing.Front) {
                                CheckinCameraController.Facing.Front
                            } else {
                                CheckinCameraController.Facing.Back
                            }
                            cameraController.bindToLifecycle(lifecycleOwner, previewView, f)
                        }
                    }
                )
            }
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
