package com.example.uitvolunteermap.features.checkin.presentation.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.AccentBlue
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.GeofenceCyan
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.PrimaryOrange
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.SuccessGreen
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextSecondary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextTertiary
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val ScreenBackground = Color(0xFFF8FCFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinHubScreen(
    state: CheckinHubUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CheckinHubUiEvent) -> Unit,
    onOpenCheckin: (CheckinHubCampaignUiModel) -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.CheckinHubScreen),
        containerColor = ScreenBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Checkin,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ScreenBackground)
        ) {
            when {
                state.isLoading && state.campaigns.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentBlue
                    )
                }

                state.errorMessage != null && state.campaigns.isEmpty() -> {
                    CheckinHubErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(CheckinHubUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { HubHeader(totalCheckins = state.totalCheckins) }
                        item { HubMap(state) }

                        if (state.campaigns.isEmpty()) {
                            item { EmptyCampaignsState() }
                        } else {
                            items(state.campaigns, key = { it.campaignId }) { campaign ->
                                CampaignCheckinCard(
                                    campaign = campaign,
                                    onCheckin = { onOpenCheckin(campaign) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HubHeader(totalCheckins: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "ĐIỂM DANH GPS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextTertiary
        )
        Text(
            text = "Địa điểm hoạt động",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Bạn đã điểm danh $totalCheckins buổi",
            fontSize = 13.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun HubMap(state: CheckinHubUiState) {
    val pinnedCampaigns = state.campaigns.filter { it.hasLocation }
    val userLatLng = if (state.userLatitude != null && state.userLongitude != null) {
        LatLng(state.userLatitude, state.userLongitude)
    } else {
        null
    }
    val focus = pinnedCampaigns.firstOrNull()?.let { LatLng(it.latitude!!, it.longitude!!) }
        ?: userLatLng
        ?: LatLng(10.8700, 106.8030) // UIT làm tâm mặc định khi chưa có dữ liệu

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(focus, 14f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            userLatLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Vị trí của bạn",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
            pinnedCampaigns.forEach { campaign ->
                val position = LatLng(campaign.latitude!!, campaign.longitude!!)
                campaign.checkInRadius?.let { radius ->
                    Circle(
                        center = position,
                        radius = radius,
                        fillColor = GeofenceCyan.copy(alpha = 0.12f),
                        strokeColor = GeofenceCyan,
                        strokeWidth = 2f
                    )
                }
                Marker(
                    state = MarkerState(position = position),
                    title = campaign.campaignName,
                    snippet = "Đã điểm danh"
                )
            }
        }
    }
}

@Composable
private fun CampaignCheckinCard(
    campaign: CheckinHubCampaignUiModel,
    onCheckin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = campaign.campaignName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = campaign.dateRange,
                fontSize = 12.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (campaign.isCheckedIn) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (campaign.checkedInAt != null) {
                            "Đã điểm danh · ${campaign.checkedInAt}"
                        } else {
                            "Đã điểm danh"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SuccessGreen
                    )
                }
            } else {
                Button(
                    onClick = onCheckin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Điểm danh",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCampaignsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Chưa có chiến dịch nào",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Khi ban tổ chức mở chiến dịch, bạn có thể điểm danh tại đây.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CheckinHubErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            fontSize = 15.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text(text = "Thử lại")
        }
    }
}
