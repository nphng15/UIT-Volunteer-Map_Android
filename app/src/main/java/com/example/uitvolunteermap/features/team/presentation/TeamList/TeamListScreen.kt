package com.example.uitvolunteermap.features.team.presentation.TeamList

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.uitvolunteermap.core.ui.theme.*
import com.example.uitvolunteermap.features.team.domain.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(
    uiState: TeamListUiState,
    onEvent: (TeamListUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(Dimens.TopBarHeight),
                title = {
                    Text(
                        text = "Danh sách Đội nhóm",
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
                            onClick = { onEvent(TeamListUiEvent.OnTeamClicked(team.id)) }
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
            .border(
                width = 1.0.dp,
                color = ColorTokens.BorderSubtle,
                // Gọi đích danh Radius24 từ theme dự án
                shape = RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.Radius24)
            ),
        shape = RoundedCornerShape(com.example.uitvolunteermap.core.ui.theme.Shapes.Radius24),
        colors = CardDefaults.cardColors(
            containerColor = ColorTokens.TextInverse
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevations.Level4)
    ) {
        Column {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamListPreview() {
    val mockTeams = listOf(
        Team(1, "Media Team", "Đội hình truyền thông", null, "Nguyễn Văn A"),
        Team(2, "Hậu cần", "Hậu cần chiến dịch", null, "Trần Thị B")
    )

    TeamListScreen(
        uiState = TeamListUiState(teams = mockTeams),
        onEvent = {}
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Danh sách có dữ liệu")
@Composable
fun PreviewTeamListWithData() {
    // Giả lập danh sách team để soi UI Card
    val mockTeams = listOf(
        Team(1, "Đội hình Media UIT", "Truyền thông", null, "Thanh Hiền"),
        Team(2, "Đội hình Hậu cần", "Logistics", null, "Văn Nam"),
        Team(3, "Đội hình Dạy học", "Giáo dục", null, "Minh Thư")
    )

    TeamListScreen(
        uiState = TeamListUiState(
            isLoading = false,
            teams = mockTeams
        ),
        onEvent = {} // Preview không cần xử lý sự kiện
    )
}

@Preview(showBackground = true, name = "Trạng thái đang tải")
@Composable
fun PreviewTeamListLoading() {
    // Soi thử cái vòng xoay CircularProgressIndicator nằm giữa màn hình chưa
    TeamListScreen(
        uiState = TeamListUiState(isLoading = true),
        onEvent = {}
    )
}

@Preview(showBackground = true, name = "Trạng thái lỗi")
@Composable
fun PreviewTeamListError() {
    // Soi thử xem thông báo lỗi hiển thị thế nào
    TeamListScreen(
        uiState = TeamListUiState(
            isLoading = false,
            errorMessage = "Không thể kết nối đến máy chủ. Vui lòng thử lại!"
        ),
        onEvent = {}
    )
}