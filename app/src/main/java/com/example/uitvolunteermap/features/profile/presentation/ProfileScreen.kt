package com.example.uitvolunteermap.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

private val ProfileBackground = Color(0xFFFBFCFF)
private val ProfileBackgroundBottom = Color(0xFFF7FAFF)
private val ProfileSurface = Color.White
private val ProfileBorder = Color(0xFFE4EAF5)
private val ProfileTextPrimary = Color(0xFF0B1A3B)
private val ProfileTextSecondary = Color(0xFF55648A)
private val ProfileTextMuted = Color(0xFF8A97B8)
private val ProfileAccent = Color(0xFFFF5A3C)
private val ProfileBrandPrimary = Color(0xFF2563FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onLogoutClick: () -> Unit,
    onBack: () -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tài khoản",
                        color = ProfileTextPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = ProfileTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ProfileBackground
                )
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
                        colors = listOf(ProfileBackground, ProfileBackgroundBottom)
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
                }

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
