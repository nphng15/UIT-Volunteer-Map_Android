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
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PersonOutline
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

enum class VolunteerBottomBarTab {
    Home,
    Map,
    Post,
    Me
}

@Composable
fun VolunteerBottomBar(
    currentTab: VolunteerBottomBarTab,
    onTabSelected: (VolunteerBottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        VolunteerBottomBarItem(
            tab = VolunteerBottomBarTab.Home,
            label = "Trang chủ",
            icon = Icons.Outlined.Home
        ),
        VolunteerBottomBarItem(
            tab = VolunteerBottomBarTab.Map,
            label = "Bản đồ",
            icon = Icons.Outlined.Map
        ),
        VolunteerBottomBarItem(
            tab = VolunteerBottomBarTab.Post,
            label = "Bài viết",
            icon = Icons.Outlined.AddBox
        ),
        VolunteerBottomBarItem(
            tab = VolunteerBottomBarTab.Me,
            label = "Tôi",
            icon = Icons.Outlined.PersonOutline
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VolunteerFlowPalette.Surface)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
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

private data class VolunteerBottomBarItem(
    val tab: VolunteerBottomBarTab,
    val label: String,
    val icon: ImageVector
)
