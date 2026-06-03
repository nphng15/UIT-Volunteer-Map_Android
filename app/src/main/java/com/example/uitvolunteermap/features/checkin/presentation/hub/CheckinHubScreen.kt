package com.example.uitvolunteermap.features.checkin.presentation.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.features.checkin.domain.entity.CampaignMoment

private val ScreenBg = Color(0xFF0B1A2B)
private val Coral = Color(0xFFFF5A3C)
private val SuccessGreen = Color(0xFF10B981)
private val ChipNavy = Color(0xFF14253A)
private val TextOnDark = Color(0xFFF8FCFF)
private val TextMutedDark = Color(0xFF9DB0C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinHubScreen(
    state: CheckinHubUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CheckinHubUiEvent) -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit,
    viewfinder: @Composable (Modifier) -> Unit,
    onShutterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.CheckinHubScreen),
        containerColor = ScreenBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Checkin,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(ScreenBg)) {
            when (state.stage) {
                CheckinHubStage.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Coral
                    )
                }

                CheckinHubStage.NoCampaign -> {
                    CenteredMessage(
                        title = "Bạn chưa thuộc chiến dịch nào",
                        body = "Khi ban tổ chức gán bạn vào một chiến dịch, bạn có thể điểm danh tại đây."
                    )
                }

                CheckinHubStage.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.errorMessage ?: "Có lỗi xảy ra.",
                            color = TextOnDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { onEvent(CheckinHubUiEvent.RefreshRequested) },
                            colors = ButtonDefaults.buttonColors(containerColor = Coral)
                        ) { Text("Thử lại") }
                    }
                }

                CheckinHubStage.Ready -> {
                    ReadyContent(
                        state = state,
                        viewfinder = viewfinder,
                        onShutterClick = onShutterClick
                    )
                }
            }

            if (state.showSuccessOverlay) {
                SuccessOverlay(onDismiss = { onEvent(CheckinHubUiEvent.SuccessOverlayDismissed) })
            }
        }
    }
}

@Composable
private fun ReadyContent(
    state: CheckinHubUiState,
    viewfinder: @Composable (Modifier) -> Unit,
    onShutterClick: () -> Unit
) {
    val campaign = state.campaign ?: return

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header + viewfinder chiếm cả 2 cột.
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Column {
                CampaignHeader(
                    campaignName = campaign.campaignName,
                    hasCheckedIn = state.hasCheckedIn,
                    checkedInAt = campaign.checkedInAt
                )
                Spacer(Modifier.height(12.dp))
                ViewfinderCard(
                    state = state,
                    viewfinder = viewfinder,
                    onShutterClick = onShutterClick
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Khoảnh khắc chiến dịch",
                    color = TextOnDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        if (state.moments.isEmpty()) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Text(
                    text = "Chưa có ảnh nào. Hãy là người đầu tiên!",
                    color = TextMutedDark,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(state.moments, key = { it.id }) { moment ->
                MomentCell(moment)
            }
        }
    }
}

@Composable
private fun CampaignHeader(
    campaignName: String,
    hasCheckedIn: Boolean,
    checkedInAt: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ĐIỂM DANH GPS",
                color = TextMutedDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = campaignName,
                color = TextOnDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        if (hasCheckedIn) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(SuccessGreen.copy(alpha = 0.16f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(4.dp))
                Text(
                    text = checkedInAt?.let { "Đã điểm danh · ${formatCheckedInDate(it)}" }
                        ?: "Đã điểm danh",
                    color = SuccessGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ViewfinderCard(
    state: CheckinHubUiState,
    viewfinder: @Composable (Modifier) -> Unit,
    onShutterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black)
    ) {
        viewfinder(Modifier.fillMaxSize())

        // Pill khoảng cách / trạng thái GPS (góc trên).
        DistancePill(
            state = state,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        )

        // Shutter.
        ShutterButton(
            enabled = state.canCapture,
            isCapturing = state.isCapturing,
            onClick = onShutterClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
        )
    }
}

@Composable
private fun DistancePill(state: CheckinHubUiState, modifier: Modifier = Modifier) {
    if (!state.locationPermissionGranted) {
        StatusPill(
            modifier = modifier,
            icon = Icons.Default.LocationOff,
            text = "Cần quyền vị trí",
            tint = Coral
        )
        return
    }
    val d = state.distanceMeters
    when {
        state.hasCheckedIn -> StatusPill(modifier, Icons.Default.CameraAlt, "Chia sẻ khoảnh khắc", SuccessGreen)
        d == null -> StatusPill(modifier, Icons.Default.LocationOn, "Đang lấy vị trí...", TextMutedDark)
        state.isWithinRadius -> StatusPill(modifier, Icons.Default.LocationOn, "Sẵn sàng điểm danh", SuccessGreen)
        else -> StatusPill(modifier, Icons.Default.LocationOn, "Cách ${formatDistance(d)} — vào khu vực", Coral)
    }
}

@Composable
private fun StatusPill(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(ChipNavy.copy(alpha = 0.92f))
            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
        Spacer(Modifier.size(6.dp))
        Text(text = text, color = TextOnDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ShutterButton(
    enabled: Boolean,
    isCapturing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ring = if (enabled) Color.White else Color.White.copy(alpha = 0.4f)
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.18f))
            .border(3.dp, ring, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCapturing) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp)
        } else {
            Box(
                modifier = Modifier
                    .testTag(VolunteerFlowTestTags.CheckinShutterButton)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (enabled) Coral else Color.Gray)
                    .let { if (enabled) it else it }
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, "Chụp", tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
    // Vùng bấm phủ toàn nút.
    if (enabled && !isCapturing) {
        Box(
            modifier = modifier
                .size(72.dp)
                .clip(CircleShape)
                .clickableNoRipple(onClick)
        )
    }
}

@Composable
private fun MomentCell(moment: CampaignMoment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(ChipNavy)
    ) {
        AsyncImage(
            model = moment.imageUrl,
            contentDescription = moment.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = moment.authorName,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            if (moment.isCheckinPhoto) {
                Spacer(Modifier.size(4.dp))
                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
            }
        }
    }
}

@Composable
private fun SuccessOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickableNoRipple(onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                null,
                tint = SuccessGreen,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Điểm danh thành công!", color = Color(0xFF0B1A3B), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Khoảnh khắc đầu tiên của bạn đã được lưu",
                color = Color(0xFF55648A),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CenteredMessage(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, color = TextOnDark, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(body, color = TextMutedDark, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

private fun formatDistance(meters: Double): String =
    if (meters >= 1000) String.format("%.1fkm", meters / 1000) else "${meters.toInt()}m"

/** Lấy phần ngày "dd/MM" từ chuỗi ISO (yyyy-MM-ddTHH:mm:ss...). */
private fun formatCheckedInDate(iso: String): String {
    return if (iso.length >= 10 && iso[4] == '-' && iso[7] == '-') {
        "${iso.substring(8, 10)}/${iso.substring(5, 7)}"
    } else {
        iso
    }
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
    return this.clickable(
        indication = null,
        interactionSource = interactionSource,
        onClick = onClick
    )
}
