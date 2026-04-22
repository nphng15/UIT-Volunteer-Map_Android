package com.example.uitvolunteermap.features.team.presentation
import androidx.compose.ui.tooling.preview.Preview

// 1. Các thư viện Compose cơ bản
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

// 2. Material Design 3 & Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*

// 3. Runtime & UI State
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 4. Design Tokens (Đảm bảo đường dẫn package này khớp với project của Hiền)
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.DesignTypography
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes

// 5. ViewModel và UiState (Theo kiến trúc Route/Screen)
import com.example.uitvolunteermap.features.team.presentation.edit.TeamEditViewModel
import com.example.uitvolunteermap.features.team.presentation.edit.TeamEditUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamEditScreen(
    teamId: Int,
    viewModel: TeamEditViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa Đội hình", style = DesignTypography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorTokens.ScreenBackgroundTop
                )
            )
        },
        containerColor = ColorTokens.ScreenBackgroundBottom
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.Spacing16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20)
        ) {
            // 1. Hero Gallery Area (Tái sử dụng layout từ Detail nhưng thêm Icon Edit)
            Box(contentAlignment = Alignment.BottomEnd) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(Dimens.HeroCardHeight),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(0.2f).fillMaxHeight(0.8f).background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(Shapes.Radius18)))
                    Box(
                        Modifier.weight(0.6f).fillMaxHeight().background(ColorTokens.BrandPrimary, RoundedCornerShape(Shapes.Radius24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                    Box(Modifier.weight(0.2f).fillMaxHeight(0.8f).background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(Shapes.Radius18)))
                }
                // Nút sửa ảnh (Floating-like icon)
                IconButton(
                    onClick = { /* Chọn ảnh */ },
                    modifier = Modifier.background(ColorTokens.BrandAccent, CircleShape).size(40.dp)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // 2. Form Input Fields (Sử dụng OutlinedTextField với Token)
            EditTextField(
                value = uiState.teamName,
                label = "Tên đội hình",
                onValueChange = { viewModel.onNameChange(it) }
            )

            EditTextField(
                value = uiState.description,
                label = "Mô tả",
                singleLine = false,
                modifier = Modifier.height(120.dp),
                onValueChange = { viewModel.onDescriptionChange(it) }
            )

            // 3. Save Button (Sử dụng RadiusPill và BrandPrimary)
            Button(
                onClick = { viewModel.updateTeam(teamId) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(Shapes.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = ColorTokens.BrandPrimary),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Lưu thay đổi", style = DesignTypography.labelLarge, color = ColorTokens.TextInverse)
                }
            }
        }
    }
}

@Composable
fun EditTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = DesignTypography.bodySmall) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Shapes.Radius18),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorTokens.BrandPrimary,
            unfocusedBorderColor = ColorTokens.BorderSubtle,
            focusedLabelColor = ColorTokens.BrandPrimary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamEditPreview() {
    val mockUiState = TeamEditUiState(
        teamName = "Đội hình Media",
        description = "Đội hình tình nguyện phụ trách điều phối nhân sự, kết nối địa bàn...",
        imageUrl = null
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa Đội hình", style = DesignTypography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorTokens.ScreenBackgroundTop
                )
            )
        },
        containerColor = ColorTokens.ScreenBackgroundBottom
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.Spacing16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20)
        ) {
            // 1. THÊM LẠI GALLERY (Phần này Hiền đang thiếu)
            Box(contentAlignment = Alignment.BottomEnd) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(Dimens.HeroCardHeight),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(0.2f).fillMaxHeight(0.8f).background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(Shapes.Radius18)).border(1.dp, ColorTokens.BorderSubtle, RoundedCornerShape(Shapes.Radius18)))
                    Box(
                        Modifier.weight(0.6f).fillMaxHeight().background(ColorTokens.BrandPrimary, RoundedCornerShape(Shapes.Radius24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                    Box(Modifier.weight(0.2f).fillMaxHeight(0.8f).background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(Shapes.Radius18)).border(1.dp, ColorTokens.BorderSubtle, RoundedCornerShape(Shapes.Radius18)))
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.offset(x = (-8).dp, y = (-8).dp).background(ColorTokens.BrandAccent, CircleShape).size(40.dp)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // 2. THÊM TÊN ĐỘI HÌNH (Dòng text displaySmall)
            Text(
                text = mockUiState.teamName,
                style = DesignTypography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = ColorTokens.TextPrimary
            )

            // 3. CÁC Ô NHẬP LIỆU (Giữ nguyên)
            EditTextField(
                value = mockUiState.teamName,
                label = "Tên đội hình",
                onValueChange = {}
            )

            EditTextField(
                value = mockUiState.description,
                label = "Mô tả",
                singleLine = false,
                modifier = Modifier.height(120.dp),
                onValueChange = {}
            )

            // 4. NÚT LƯU THAY ĐỔI
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(Shapes.RadiusPill),
                colors = ButtonDefaults.buttonColors(containerColor = ColorTokens.BrandPrimary)
            ) {
                Text("Lưu thay đổi", style = DesignTypography.labelLarge, color = ColorTokens.TextInverse)
            }
        }
    }
}