package com.example.uitvolunteermap.features.team.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.DesignTypography
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.team.domain.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    team: Team,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chi tiết đội hình",
                        style = DesignTypography.titleLarge,
                        color = ColorTokens.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = ColorTokens.TextPrimary)
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
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // 1. Tên đội hình
            Text(
                text = team.name,
                style = DesignTypography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = ColorTokens.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Spacing24, bottom = Dimens.Spacing8)
            )

            // 2. Formation Hero (Tỷ lệ 0.2 - 0.6 - 0.2 với Icon Image)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.HeroCardHeight)
                    .padding(horizontal = Dimens.Spacing16),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroPhotoBox(Modifier.weight(0.2f), heightFraction = 0.8f, radius = Shapes.Radius18)
                HeroPhotoBox(Modifier.weight(0.6f), heightFraction = 1f, radius = Shapes.Radius24, isMain = true)
                HeroPhotoBox(Modifier.weight(0.2f), heightFraction = 0.8f, radius = Shapes.Radius18)
            }

            // 3. Mô tả
            Text(
                text = team.description,
                style = DesignTypography.bodyMedium,
                color = ColorTokens.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Spacing20)
            )

            // 4. Ban chỉ huy (NẰM NGANG)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.Spacing20),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ban chỉ huy",
                    style = DesignTypography.headlineMedium.copy(color = ColorTokens.TextPrimary),
                    modifier = Modifier.padding(bottom = Dimens.Spacing20)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LeaderHorizontalItem("LT", "Chỉ huy trưởng", "Lê Thanh")
                    LeaderHorizontalItem("MN", "Điều phối", "Minh Ngọc")
                    LeaderHorizontalItem("TK", "Hậu cần", "Thu Kỳ")
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Spacing32))

            // 5. Hoạt động (Grid ảnh)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.Spacing20),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hoạt động",
                    style = DesignTypography.headlineMedium.copy(color = ColorTokens.TextPrimary),
                    modifier = Modifier.padding(bottom = Dimens.Spacing16)
                )

                // Row 1: Nút Add + 2 ảnh
                Row(Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActivityAddBox(Modifier.weight(1f))
                    ActivityPhotoBox(Modifier.weight(1f), "Ảnh 1")
                    ActivityPhotoBox(Modifier.weight(1f), "Ảnh 2")
                }
                Spacer(Modifier.height(12.dp))
                // Row 2: 3 ảnh
                Row(Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActivityPhotoBox(Modifier.weight(1f), "Ảnh 3")
                    ActivityPhotoBox(Modifier.weight(1f), "Ảnh 4")
                    ActivityPhotoBox(Modifier.weight(1f), "Ảnh 5")
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Spacing32))
        }
    }
}

@Composable
fun HeroPhotoBox(modifier: Modifier, heightFraction: Float, radius: androidx.compose.ui.unit.Dp, isMain: Boolean = false) {
    Box(
        modifier = modifier
            .fillMaxHeight(heightFraction)
            .background(
                color = if (isMain) ColorTokens.BrandPrimary.copy(alpha = 0.05f) else ColorTokens.ScreenBackgroundTop,
                shape = RoundedCornerShape(radius)
            )
            .border(1.dp, ColorTokens.BorderSubtle, RoundedCornerShape(radius)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = ColorTokens.TextSecondary.copy(alpha = 0.3f),
            modifier = Modifier.size(if (isMain) 48.dp else 32.dp)
        )
    }
}

@Composable
fun LeaderHorizontalItem(initials: String, role: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(86.dp),
            shape = CircleShape,
            color = ColorTokens.ScreenBackgroundTop,
            border = BorderStroke(1.dp, ColorTokens.BorderSubtle)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(initials, style = DesignTypography.headlineMedium, color = ColorTokens.TextSecondary.copy(alpha = 0.5f))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(role, style = DesignTypography.labelLarge, color = ColorTokens.TextPrimary)
        Text(name, style = DesignTypography.bodySmall, color = ColorTokens.TextSecondary)
    }
}

@Composable
fun ActivityAddBox(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFD8D2C7), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun ActivityPhotoBox(modifier: Modifier, label: String) {
    Box(
        modifier = modifier.fillMaxSize().background(ColorTokens.ScreenBackgroundTop, RoundedCornerShape(Shapes.Radius18)),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = DesignTypography.labelMedium, color = ColorTokens.TextSecondary.copy(alpha = 0.5f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamDetailPreview() {
    val mockTeam = Team(
        id = 1,
        name = "Đội hình Media",
        description = "Đội hình tình nguyện phụ trách điều phối nhân sự, kết nối địa bàn và theo dõi tiến độ hoạt động trong suốt chiến dịch.",
        leaderName = "Lê Thanh",
        imageUrl = null,
        attachments = listOf("1", "2", "3")
    )
    TeamDetailScreen(team = mockTeam, onBack = {})
}