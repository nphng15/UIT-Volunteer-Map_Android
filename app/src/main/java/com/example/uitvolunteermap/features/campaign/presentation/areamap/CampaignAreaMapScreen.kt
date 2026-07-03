package com.example.uitvolunteermap.features.campaign.presentation.areamap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import org.osmdroid.events.MapEventsReceiver
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint
import java.io.File
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

private val PinPalette = listOf(
    0xFFE53935, 0xFF1E88E5, 0xFF43A047, 0xFFFB8C00,
    0xFF8E24AA, 0xFF00897B, 0xFFF4511E, 0xFF3949AB
).map { it.toInt() }

private fun teamColorInt(teamId: Int): Int = PinPalette[teamId.mod(PinPalette.size)]

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignAreaMapScreen(
    state: CampaignAreaMapUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignAreaMapUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = File(context.cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tiles")
        }
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(11.0)
            controller.setCenter(GeoPoint(10.8231, 106.6297)) // HCMC
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    LaunchedEffect(state.points, state.pendingLocation, state.canMark) {
        mapView.overlays.clear()
        if (state.canMark) {
            mapView.overlays.add(
                MapEventsOverlay(
                    object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            onEvent(CampaignAreaMapUiEvent.MapTapped(p.latitude, p.longitude))
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }
                )
            )
        }
        val hiddenTeamId = state.pendingLocation?.let {
            if (state.isAdmin) null else state.checkInTeam?.id
        }
        state.points.filterNot { it.teamId == hiddenTeamId }.forEach { point ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(point.latitude, point.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = makePin(context, teamColorInt(point.teamId))
                title = point.name
                snippet = point.teamName
            }
            mapView.overlays.add(marker)
        }
        state.points.firstOrNull { it.teamId != hiddenTeamId }?.let {
            mapView.controller.setCenter(GeoPoint(it.latitude, it.longitude))
            mapView.controller.setZoom(12.0)
        }
        mapView.invalidate()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Khu vực hoạt động", style = MaterialTheme.typography.titleMedium)
                        if (state.campaignTitle.isNotBlank()) {
                            Text(
                                "${state.campaignTitle} · ${state.pointCount} điểm",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CampaignAreaMapUiEvent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.canMark) {
                ExtendedFloatingActionButton(
                    onClick = { onEvent(CampaignAreaMapUiEvent.MarkHereClicked) },
                    icon = { Icon(Icons.Rounded.AddLocationAlt, contentDescription = null) },
                    text = { Text("Lưu vị trí đội") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            if (state.canMark) {
                Text(
                    text = "Chạm vào bản đồ để chọn điểm check-in hoặc dùng nút GPS.",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            TeamLegend(
                points = state.points,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }

    if (state.pendingLocation != null) {
        MarkPointDialog(
            teams = state.selectableTeams,
            checkInTeam = state.checkInTeam,
            canSelectAnyCheckInTeam = state.canSelectAnyCheckInTeam,
            isSaving = state.isSaving,
            onConfirm = { teamId, teamName, name ->
                onEvent(CampaignAreaMapUiEvent.ConfirmLocation(teamId, teamName, name))
            },
            onDismiss = { onEvent(CampaignAreaMapUiEvent.DialogDismissed) }
        )
    }
}

@Composable
private fun TeamLegend(
    points: List<TeamVisitPoint>,
    modifier: Modifier = Modifier
) {
    val teams = points.distinctBy { it.teamId }
    if (teams.isEmpty()) return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        teams.forEach { point ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(teamColorInt(point.teamId)))
                )
                Text(
                    text = " ${point.teamName}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkPointDialog(
    teams: List<TeamOption>,
    checkInTeam: TeamOption?,
    canSelectAnyCheckInTeam: Boolean,
    isSaving: Boolean,
    onConfirm: (Int, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    val selectedTeam = teams.getOrNull(selectedIndex)
    val targetCheckInTeam = if (canSelectAnyCheckInTeam) selectedTeam else checkInTeam

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lưu vị trí đội") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (teams.isEmpty()) {
                    Text(
                        "Chưa tải được danh sách đội. Hãy thử lại.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text("Chọn đội cho điểm hoạt động:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        teams.forEachIndexed { index, team ->
                            FilterChip(
                                selected = index == selectedIndex,
                                onClick = { selectedIndex = index },
                                label = { Text(team.name) }
                            )
                        }
                    }
                }

                Text(
                    text = if (canSelectAnyCheckInTeam) {
                        "Vị trí này sẽ được lưu đồng thời làm điểm đội và điểm check-in cho đội đang chọn."
                    } else {
                        checkInTeam?.let { "Vị trí này sẽ được lưu đồng thời làm điểm đội và điểm check-in cho ${it.name}." }
                            ?: "Bạn chưa được gán đội trong chiến dịch này nên chưa thể lưu điểm check-in."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("Tên điểm hoạt động (tuỳ chọn)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving && targetCheckInTeam != null,
                onClick = { targetCheckInTeam?.let { onConfirm(it.id, it.name, name) } }
            ) { Text("Lưu điểm đội & check-in") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        }
    )
}

/** A circular colored map pin so each team is visually distinct. */
private fun makePin(context: Context, color: Int): Drawable {
    val size = 48
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val radius = size / 2f
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = AndroidColor.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    canvas.drawCircle(radius, radius, radius - 4f, fill)
    canvas.drawCircle(radius, radius, radius - 4f, stroke)
    return BitmapDrawable(context.resources, bmp)
}
