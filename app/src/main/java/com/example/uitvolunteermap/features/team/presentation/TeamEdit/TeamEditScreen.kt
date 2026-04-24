package com.example.uitvolunteermap.features.team.presentation.TeamEdit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
// CHÚ Ý: Không import androidx.compose.material3.Shapes ở đây

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamEditScreen(
    uiState: TeamEditUiState,
    onEvent: (TeamEditUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa Đội hình", style = DesignTypography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TeamEditUiEvent.OnBackClicked) }) {
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
            // 1. Hero Gallery Area
            Box(contentAlignment = Alignment.BottomEnd) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(Dimens.HeroCardHeight),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sửa lỗi Radius18 và Radius24
                    Box(Modifier.weight(0.2f).fillMaxHeight(0.8f).background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.Radius18)))
                    Box(
                        Modifier.weight(0.6f).fillMaxHeight().background(ColorTokens.BrandPrimary, RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.Radius24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                    Box(Modifier.weight(0.2f).fillMaxHeight(0.8f).background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.Radius18)))
                }
                IconButton(
                    onClick = { onEvent(TeamEditUiEvent.OnEditAttachmentsClicked) },
                    modifier = Modifier.background(ColorTokens.BrandAccent, CircleShape).size(40.dp)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            EditTextField(
                value = uiState.teamName,
                label = "Tên đội hình",
                onValueChange = { onEvent(TeamEditUiEvent.OnNameChanged(it)) }
            )

            EditTextField(
                value = uiState.description,
                label = "Mô tả",
                singleLine = false,
                modifier = Modifier.height(120.dp),
                onValueChange = { onEvent(TeamEditUiEvent.OnDescriptionChanged(it)) }
            )

            // Sửa lỗi RadiusPill
            Button(
                onClick = { onEvent(TeamEditUiEvent.OnSaveClicked) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.RadiusPill),
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
        // Sửa lỗi Radius18 cuối cùng
        shape = RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.Radius18),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorTokens.BrandPrimary,
            unfocusedBorderColor = ColorTokens.BorderSubtle,
            focusedLabelColor = ColorTokens.BrandPrimary
        )
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Màn hình chỉnh sửa - Có dữ liệu")
@Composable
fun PreviewTeamEditWithData() {
    // Giả lập một UiState đã có thông tin
    val mockState = TeamEditUiState(
        teamName = "Đội hình Media UIT",
        description = "Đội hình chuyên trách về hình ảnh và truyền thông cho các chiến dịch tình nguyện tại trường.",
        isLoading = false
    )

    TeamEditScreen(
        uiState = mockState,
        onEvent = {} // Truyền lambda rỗng vì Preview không cần xử lý logic
    )
}

@Preview(showBackground = true, name = "Màn hình chỉnh sửa - Đang lưu")
@Composable
fun PreviewTeamEditLoading() {
    // Soi thử xem cái vòng xoay CircularProgressIndicator hiện lên có đẹp không
    val loadingState = TeamEditUiState(
        teamName = "Đội hình Media UIT",
        description = "Đang cập nhật dữ liệu...",
        isLoading = true
    )

    TeamEditScreen(
        uiState = loadingState,
        onEvent = {}
    )
}