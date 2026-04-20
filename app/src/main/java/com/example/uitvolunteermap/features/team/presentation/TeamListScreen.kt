package com.example.uitvolunteermap.features.team.presentation
import TeamListUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.Elevations
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.team.domain.model.Team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.core.ui.theme.DesignTypography

@Composable
fun TeamListRoute(
    viewModel: TeamListViewModel,
    onTeamClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    TeamListScreen(
        uiState = uiState,
        onTeamClick = onTeamClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(
    uiState: TeamListUiState,
    onTeamClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(Dimens.TopBarHeight),
                title = {
                    Text(
                        text = "Danh sách Đội nhóm",
                        // Sử dụng headlineMedium với FontWeight.ExtraBold để đạt độ đậm yêu cầu
                        style = DesignTypography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = ColorTokens.TextPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorTokens.ScreenBackgroundTop
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Sử dụng màu nền vàng nhạt từ Token hệ thống
                .background(ColorTokens.ScreenBackgroundTop)
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ColorTokens.BrandPrimary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Dimens.Spacing16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.teams) { team ->
                        TeamCard(
                            team = team,
                            onClick = { onTeamClick(team.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamCard(team: Team, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            // THIẾT KẾ VIỀN ĐẬM: Sử dụng BrandPrimary và độ dày 2.dp (không hardcode màu)
            .border(
                width = 1.0.dp,
                color = ColorTokens.BorderSubtle,
                shape = RoundedCornerShape(Shapes.Radius24)
            ),
        shape = RoundedCornerShape(Shapes.Radius24),
        colors = CardDefaults.cardColors(
            containerColor = ColorTokens.TextInverse // Nền trắng để tương phản với viền đậm
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevations.Level4)
    ) {
        Column {
            // Phần Header ảnh của Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CampaignCoverHeight)
                    .background(ColorTokens.TextSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = ColorTokens.BrandPrimary,
                    modifier = Modifier.size(Dimens.IconMedium * 2)
                )
            }

            Column(modifier = Modifier.padding(Dimens.Spacing16)) {
                Text(
                    text = team.name,
                    // Sử dụng FontWeight.ExtraBold để tiêu đề cực đậm
                    style = DesignTypography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = ColorTokens.TextPrimary
                )

                Spacer(modifier = Modifier.height(Dimens.Spacing8))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        Surface(
                            modifier = Modifier
                                .size(Dimens.Spacing24)
                                // Hiệu ứng avatar đè lên nhau sử dụng Spacing Token
                                .offset(x = -(Dimens.Spacing8 * index.toFloat())),
                            shape = CircleShape,
                            color = ColorTokens.BrandAccentSoft,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ColorTokens.BrandPrimary,
                                modifier = Modifier.padding(Dimens.Spacing2)
                            )
                        }
                    }
                    Text(
                        text = "Leader: ${team.leaderName}",
                        // Font chữ nội dung in đậm mạnh mẽ
                        style = DesignTypography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = Dimens.Spacing4),
                        color = ColorTokens.TextPrimary
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun TeamListPreview() {
    // Tự tạo data giả ngay trong hàm này để Preview vẽ ra
    val mockTeams = listOf(
        Team(1, "Media Team", "Đội hình truyền thông", null, "Nguyễn Văn A"),
        Team(2, "Hậu cần", "Hậu cần chiến dịch", null, "Trần Thị B")
    )

    // Gọi màn hình UI và truyền data giả vào
    TeamListScreen(
        uiState = TeamListUiState(teams = mockTeams),
        onTeamClick = {}
    )
}