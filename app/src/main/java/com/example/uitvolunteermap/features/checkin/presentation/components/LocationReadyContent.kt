package com.example.uitvolunteermap.features.checkin.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiEvent
import com.example.uitvolunteermap.features.checkin.presentation.GpsCheckinUiState
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.GeofenceCyan
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.PrimaryOrange
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextPrimary
import com.example.uitvolunteermap.features.checkin.presentation.components.GpsCheckinTokens.TextSecondary

@Composable
internal fun LocationReadyContent(
    state: GpsCheckinUiState,
    onEvent: (GpsCheckinUiEvent) -> Unit
) {
    val campaign = state.selectedCampaign ?: return
    val userLat = state.userLatitude ?: return
    val userLng = state.userLongitude ?: return

    val campaignLatLng = LatLng(campaign.latitude, campaign.longitude)
    val userLatLng = LatLng(userLat, userLng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(campaignLatLng, 16f)
    }

    Spacer(modifier = Modifier.height(12.dp))

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
            Circle(
                center = campaignLatLng,
                radius = campaign.checkInRadius,
                fillColor = GeofenceCyan.copy(alpha = 0.12f),
                strokeColor = GeofenceCyan,
                strokeWidth = 2f
            )
            Marker(
                state = MarkerState(position = campaignLatLng),
                title = campaign.campaignName,
                snippet = "Điểm điểm danh"
            )
            Marker(
                state = MarkerState(position = userLatLng),
                title = "Vị trí của bạn",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = campaign.campaignName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bán kính cho phép: ${campaign.checkInRadius.toInt()}m",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
        onClick = { onEvent(GpsCheckinUiEvent.CheckinClicked) },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Điểm danh ngay",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
