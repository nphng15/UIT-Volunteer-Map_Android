package com.example.uitvolunteermap.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.core.ui.VolunteerTopBar
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

private val ProfileBackground = VolunteerFlowPalette.Background
private val ProfileBackgroundBottom = VolunteerFlowPalette.BackgroundBottom
private val ProfileSurface = VolunteerFlowPalette.Surface
private val ProfileBorder = VolunteerFlowPalette.Border
private val ProfileTextPrimary = VolunteerFlowPalette.TextPrimary
private val ProfileTextSecondary = VolunteerFlowPalette.TextSecondary
private val ProfileTextMuted = VolunteerFlowPalette.TextMuted
private val ProfileAccent = VolunteerFlowPalette.BrandAccent
private val ProfileBrandPrimary = VolunteerFlowPalette.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onLogoutClick: () -> Unit,
    onBack: () -> Unit,
    onCampaignClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
    onChooseCheckInPointClick: (Int) -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileBackground,
        topBar = {
            // Profile là tab gốc (bottom-nav) nên không có nút quay lại.
            VolunteerTopBar(
                title = "Tài khoản",
                titleColor = ProfileTextPrimary,
                containerColor = ProfileBackground
            )
        },
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Me,
                onTabSelected = { selectedTab ->
                    if (selectedTab != VolunteerBottomBarTab.Me) {
                        onTabSelected(selectedTab)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VolunteerFlowPalette.BackgroundTop,
                            ProfileBackground,
                            ProfileBackgroundBottom
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(8.dp, CircleShape, ambientColor = ProfileBrandPrimary.copy(alpha = 0.15f))
                        .clip(CircleShape)
                        .background(ProfileBrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.username.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 18.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = ProfileBrandPrimary.copy(alpha = 0.08f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(ProfileSurface)
                        .border(1.dp, ProfileBorder, RoundedCornerShape(24.dp))
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileInfoRow(label = "Tên đăng nhập", value = state.username)
                    ProfileInfoRow(label = "Vai trò", value = state.role.displayName())
                    ProfileInfoRow(label = "Mã tài khoản", value = "#${state.accountId}")

                    state.fullName?.let { ProfileInfoRow(label = "Họ tên", value = it) }
                    state.mssv?.let { ProfileInfoRow(label = "MSSV", value = it) }
                    state.className?.let { ProfileInfoRow(label = "Lớp", value = it) }
                    state.email?.let { ProfileInfoRow(label = "Email", value = it) }
                    state.phoneNumber?.let { ProfileInfoRow(label = "Số điện thoại", value = it) }
                    state.createdAt?.let { ProfileInfoRow(label = "Ngày tạo", value = it) }

                    if (state.isProfileLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = ProfileBrandPrimary,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                MyCampaignCard(
                    state = state,
                    onCampaignClick = onCampaignClick,
                    onTeamClick = onTeamClick,
                    onChooseCheckInPointClick = onChooseCheckInPointClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Logout button
                Button(
                    onClick = onLogoutClick,
                    enabled = !state.isLoggingOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfileAccent,
                        contentColor = Color.White,
                        disabledContainerColor = ProfileAccent.copy(alpha = 0.45f)
                    )
                ) {
                    if (state.isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Đăng xuất",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyCampaignCard(
    state: ProfileUiState,
    onCampaignClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
    onChooseCheckInPointClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = ProfileBrandPrimary.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(ProfileSurface)
            .border(1.dp, ProfileBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Chiến dịch bạn đang tham gia",
            style = MaterialTheme.typography.titleMedium,
            color = ProfileTextPrimary,
            fontWeight = FontWeight.ExtraBold
        )

        when {
            state.isMyCampaignLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ProfileBrandPrimary,
                        strokeWidth = 2.dp
                    )
                }
            }

            state.myCampaign != null -> {
                val campaign = state.myCampaign
                ProfileInfoRow(label = "Chiến dịch", value = campaign.campaignName)
                ProfileInfoRow(
                    label = "Thời gian",
                    value = "${campaign.startDate} → ${campaign.endDate}"
                )
                ProfileInfoRow(
                    label = "Đội hình của bạn",
                    value = campaign.teamName ?: "Chưa được gán đội"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onCampaignClick(campaign.campaignId) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Chiến dịch") }

                    OutlinedButton(
                        onClick = { campaign.teamId?.let(onTeamClick) },
                        enabled = campaign.teamId != null,
                        modifier = Modifier.weight(1f)
                    ) { Text("Đội hình") }
                }

                Button(
                    onClick = { onChooseCheckInPointClick(campaign.campaignId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfileBrandPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Chọn điểm check-in", fontWeight = FontWeight.SemiBold)
                }
            }

            state.myCampaignErrorMessage != null -> {
                Text(
                    text = state.myCampaignErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> {
                Text(
                    text = "Bạn chưa tham gia chiến dịch nào.",
                    color = ProfileTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ProfileTextMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = ProfileTextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun UserRole.displayName(): String = when (this) {
    UserRole.ADMIN -> "Quản trị viên"
    UserRole.LEADER -> "Trưởng đội"
    UserRole.VOLUNTEER -> "Tình nguyện viên"
    UserRole.GUEST -> "Khách"
}
