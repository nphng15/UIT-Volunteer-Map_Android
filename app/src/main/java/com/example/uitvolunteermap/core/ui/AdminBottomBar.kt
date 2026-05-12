package com.example.uitvolunteermap.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

enum class AdminBottomBarTab {
    Dashboard,
    Accounts,
    Campaigns,
    Teams,
    Posts
}

@Composable
fun AdminBottomBar(
    currentTab: AdminBottomBarTab,
    onTabSelected: (AdminBottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        AdminBottomBarItem(AdminBottomBarTab.Dashboard, "Tổng quan", Icons.Outlined.Dashboard),
        AdminBottomBarItem(AdminBottomBarTab.Accounts, "Tài khoản", Icons.Outlined.ManageAccounts),
        AdminBottomBarItem(AdminBottomBarTab.Campaigns, "Chiến dịch", Icons.Outlined.Campaign),
        AdminBottomBarItem(AdminBottomBarTab.Teams, "Đội", Icons.Outlined.Group),
        AdminBottomBarItem(AdminBottomBarTab.Posts, "Bài viết", Icons.AutoMirrored.Outlined.Article)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VolunteerFlowPalette.Surface)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.tab == currentTab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(item.tab) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = if (isSelected) {
                                VolunteerFlowPalette.BrandPrimary.copy(alpha = 0.12f)
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) {
                            VolunteerFlowPalette.BrandPrimary
                        } else {
                            VolunteerFlowPalette.TextMuted
                        }
                    )
                }
                Text(
                    text = item.label,
                    color = if (isSelected) {
                        VolunteerFlowPalette.BrandPrimary
                    } else {
                        VolunteerFlowPalette.TextMuted
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class AdminBottomBarItem(
    val tab: AdminBottomBarTab,
    val label: String,
    val icon: ImageVector
)
