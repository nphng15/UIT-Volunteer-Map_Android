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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.vector.ImageVector
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

// MagicPath light theme tokens
private val ScreenBg = Color(0xFFF8FCFF)
private val Coral = Color(0xFFFF5A3C)
private val CoralPale = Color(0xFFFFD5CE)
private val CoralTint = Color(0x1AFF5A3C)
private val SuccessGreen = Color(0xFF10B981)
private val Navy = Color(0xFF0B1A3B)
private val SubText = Color(0xFF55648A)
private val Muted = Color(0xFF8A97B8)
private val Border = Color(0xFFE5EBF5)
private val PanelBg = Color(0xFFFFFFFF)
private val DisabledGray = Color(0xFFC5CDD8)

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
                CheckinHubStage.NoCampaign -> CenteredMessage(
                    title = "Bạn chưa thuộc chiến dịch nào",
                    body = "Khi ban tổ chức gán bạn vào một chiến dịch, bạn có thể điểm danh tại đây."
                )
                CheckinHubStage.Error -> ErrorState(
                    message = state.errorMessage ?: "Có lỗi xảy ra.",
                    onRetry = { onEvent(CheckinHubUiEvent.RefreshRequested) }
                )
                CheckinHubStage.Ready -> ReadyContent(
                    state = state,
                    onEvent = onEvent,
                    viewfinder = viewfinder,
                    onShutterClick = onShutterClick
                )
            }

            if (state.showSuccessOverlay) {
                SuccessOverlay(onDismiss = { onEvent(CheckinHubUiEvent.SuccessOverlayDismissed) })
            }

            state.viewingMoment?.let { moment ->
                MomentDetailOverlay(
                    moment = moment,
                    canDelete = state.canDeleteViewingMoment,
                    onDismiss = { onEvent(CheckinHubUiEvent.MomentViewerDismissed) },
                    onDelete = { onEvent(CheckinHubUiEvent.DeleteMomentRequested(moment.id)) }
                )
            }
        }
    }
}

@Composable
private fun ReadyContent(
    state: CheckinHubUiState,
    onEvent: (CheckinHubUiEvent) -> Unit,
    viewfinder: @Composable (Modifier) -> Unit,
    onShutterClick: () -> Unit
) {
    val campaign = state.campaign ?: return

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item(span = { GridItemSpan(3) }) {
            Column {
                CampaignHeader(
                    campaignName = campaign.campaignName,
                    hasCheckedIn = state.hasCheckedIn,
                    checkedInAt = campaign.checkedInAt
                )
                Spacer(Modifier.height(12.dp))
                ViewfinderCard(
                    state = state,
                    onEvent = onEvent,
                    viewfinder = viewfinder,
                    onShutterClick = onShutterClick
                )
                Spacer(Modifier.height(16.dp))
                MomentsHeader(count = state.moments.size)
                Spacer(Modifier.height(8.dp))
            }
        }

        if (state.momentsError) {
            item(span = { GridItemSpan(3) }) {
                MomentsError(onRetry = { onEvent(CheckinHubUiEvent.RefreshRequested) })
            }
        } else if (state.moments.isEmpty()) {
            item(span = { GridItemSpan(3) }) {
                EmptyMoments()
            }
        } else {
            items(state.moments, key = { it.id }) { moment ->
                MomentCell(
                    moment = moment,
                    onClick = { onEvent(CheckinHubUiEvent.MomentSelected(moment.id)) }
                )
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
                color = Muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = campaignName,
                color = Navy,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        if (hasCheckedIn) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(SuccessGreen.copy(alpha = 0.12f))
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
    onEvent: (CheckinHubUiEvent) -> Unit,
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
        // Live camera preview (luôn render để tránh flash đen khi quay lại)
        viewfinder(Modifier.fillMaxSize())

        // Lớp xem trước ảnh vừa chụp (đè lên live khi Reviewing/Sending)
        if (state.previewFile != null) {
            AsyncImage(
                model = state.previewFile,
                contentDescription = "Ảnh vừa chụp",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (!state.cameraPermissionGranted) {
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFF111827)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CameraAlt, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(8.dp))
                Text("Cần quyền camera", color = Color.White.copy(0.7f), fontSize = 12.sp)
            }
        }

        // Pill khoảng cách / trạng thái GPS (góc trên phải) — chỉ hiện ở Live
        if (state.shutterMode == ShutterMode.Live) {
            DistancePill(
                state = state,
                onRetry = { onEvent(CheckinHubUiEvent.RetryLocationRequested) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            // Flip camera (góc trên trái) — chỉ ở Live
            if (state.cameraPermissionGranted) {
                CircleIconButton(
                    icon = Icons.Default.Cameraswitch,
                    desc = "Đổi camera",
                    onClick = { onEvent(CheckinHubUiEvent.CameraFlipRequested) },
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                )
            }
        }

        // Cụm nút dưới đáy: thay đổi theo shutterMode.
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state.shutterMode) {
                ShutterMode.Live -> ShutterButton(
                    enabled = state.canCapture,
                    onClick = onShutterClick
                )
                ShutterMode.Reviewing -> Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        icon = Icons.Default.Close,
                        desc = "Huỷ ảnh",
                        onClick = { onEvent(CheckinHubUiEvent.PreviewDismissed) }
                    )
                    SendButton(
                        sending = false,
                        onClick = { onEvent(CheckinHubUiEvent.PreviewSendRequested) }
                    )
                    Spacer(Modifier.size(44.dp)) // giữ cân đối với Close
                }
                ShutterMode.Sending -> SendButton(sending = true, onClick = {})
            }
        }
    }
}

@Composable
private fun DistancePill(
    state: CheckinHubUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, text, tint, retry) = when {
        !state.locationPermissionGranted -> StatusPillSpec(Icons.Default.LocationOff, "Cần quyền vị trí", Coral, false)
        state.locationError -> StatusPillSpec(Icons.Default.LocationOff, "Lỗi GPS · Thử lại", Coral, true)
        state.campaignHasNoLocation -> StatusPillSpec(Icons.Default.LocationOff, "Chiến dịch chưa có GPS", Muted, false)
        state.hasCheckedIn -> StatusPillSpec(Icons.Default.CameraAlt, "Chia sẻ khoảnh khắc", SuccessGreen, false)
        state.distanceMeters == null -> StatusPillSpec(Icons.Default.LocationOn, "Đang lấy vị trí…", Muted, true)
        state.isWithinRadius -> StatusPillSpec(Icons.Default.LocationOn, "Sẵn sàng điểm danh", SuccessGreen, false)
        else -> StatusPillSpec(Icons.Default.LocationOn, "Cách ${formatDistance(state.distanceMeters)} — vào khu vực", Coral, false)
    }
    StatusPill(
        modifier = modifier.let { if (retry) it.clickableNoRipple(onRetry) else it },
        icon = icon, text = text, tint = tint
    )
}

private data class StatusPillSpec(val icon: ImageVector, val text: String, val tint: Color, val retry: Boolean)

@Composable
private fun StatusPill(modifier: Modifier, icon: ImageVector, text: String, tint: Color) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, tint.copy(alpha = 0.45f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
        Spacer(Modifier.size(6.dp))
        Text(text = text, color = Navy, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ShutterButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ring = if (enabled) Color.White else Color.White.copy(alpha = 0.4f)
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.18f))
            .border(3.dp, ring, CircleShape)
            .let { if (enabled) it.clickableNoRipple(onClick) else it },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .testTag(VolunteerFlowTestTags.CheckinShutterButton)
                .size(56.dp)
                .clip(CircleShape)
                .background(if (enabled) Coral else DisabledGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CameraAlt, "Chụp", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun SendButton(sending: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Coral)
            .border(3.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            .let { if (sending) it else it.clickableNoRipple(onClick) },
        contentAlignment = Alignment.Center
    ) {
        if (sending) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp)
        } else {
            Icon(Icons.AutoMirrored.Filled.Send, "Gửi", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, desc, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun MomentsHeader(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "📷 Khoảnh khắc chiến dịch",
            color = Navy,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.size(8.dp))
        if (count > 0) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CoralTint)
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                Text("$count ảnh", color = Coral, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyMoments() {
    Text(
        text = "Chưa có ảnh nào. Hãy là người đầu tiên!",
        color = Muted,
        fontSize = 13.sp,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun MomentsError(onRetry: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CoralTint)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Không tải được khoảnh khắc.",
            color = Coral,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            "Thử lại",
            color = Coral,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickableNoRipple(onRetry)
        )
    }
}

@Composable
private fun MomentCell(moment: CampaignMoment, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Border)
            .clickableNoRipple(onClick)
    ) {
        AsyncImage(
            model = moment.imageUrl,
            contentDescription = moment.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (moment.isCheckinPhoto) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen)
                    .padding(3.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
private fun MomentDetailOverlay(
    moment: CampaignMoment,
    canDelete: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickableNoRipple(onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ảnh fullscreen với cùng aspect ratio như viewfinder live (0.78)
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.78f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = moment.imageUrl,
                    contentDescription = moment.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                CircleIconButton(
                    icon = Icons.Default.Close,
                    desc = "Đóng",
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Tác giả + team + thời gian
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PanelBg)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CoralTint),
                    contentAlignment = Alignment.Center
                ) {
                    if (!moment.authorAvatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = moment.authorAvatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = moment.authorName.take(1).uppercase(),
                            color = Coral,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(moment.authorName, color = Navy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    val parts = listOfNotNull(
                        moment.authorTeamName?.takeIf { it.isNotBlank() },
                        formatRelativeTime(moment.createdAt)
                    )
                    if (parts.isNotEmpty()) {
                        Text(parts.joinToString(" · "), color = SubText, fontSize = 12.sp)
                    }
                    if (moment.isCheckinPhoto) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Ảnh điểm danh", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (canDelete) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CoralTint)
                            .clickableNoRipple(onDelete),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, "Xoá", tint = Coral, modifier = Modifier.size(20.dp))
                    }
                }
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
                .background(PanelBg)
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Điểm danh thành công!", color = Navy, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Khoảnh khắc đầu tiên của bạn đã được lưu",
                color = SubText,
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
        Text(title, color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(body, color = SubText, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = Navy, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Coral)) {
            Text("Thử lại", color = Color.White)
        }
    }
}

private fun formatDistance(meters: Double): String =
    if (meters >= 1000) String.format("%.1fkm", meters / 1000) else "${meters.toInt()}m"

/** "dd/MM" từ chuỗi ISO yyyy-MM-ddTHH:mm:ss... */
private fun formatCheckedInDate(iso: String): String =
    if (iso.length >= 10 && iso[4] == '-' && iso[7] == '-')
        "${iso.substring(8, 10)}/${iso.substring(5, 7)}"
    else iso

/**
 * Định dạng thời gian tương đối (vd: "5 phút trước", "2 giờ trước", "3 ngày trước").
 * Dùng System.currentTimeMillis tại thời điểm gọi để tính.
 */
private fun formatRelativeTime(iso: String): String? {
    val createdMs = parseIsoToMillis(iso) ?: return null
    val nowMs = System.currentTimeMillis()
    val diffSec = (nowMs - createdMs) / 1000
    return when {
        diffSec < 60 -> "Vừa xong"
        diffSec < 3600 -> "${diffSec / 60} phút trước"
        diffSec < 86400 -> "${diffSec / 3600} giờ trước"
        diffSec < 86400L * 7 -> "${diffSec / 86400} ngày trước"
        else -> formatCheckedInDate(iso)
    }
}

/** Parse "yyyy-MM-ddTHH:mm:ss[.SSS]Z" hoặc tương tự sang epoch millis (UTC). */
private fun parseIsoToMillis(iso: String): Long? = runCatching {
    java.time.Instant.parse(iso).toEpochMilli()
}.getOrNull()

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
    return this.clickable(
        indication = null,
        interactionSource = interactionSource,
        onClick = onClick
    )
}
