package com.example.uitvolunteermap.features.attendance.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.attendance.presentation.AttendanceFilter
import com.example.uitvolunteermap.features.attendance.presentation.AttendanceUiState
import com.example.uitvolunteermap.features.attendance.presentation.ManagedTeamUiModel

@Composable
fun AttendanceFilterRow(
    state: AttendanceUiState,
    onFilterChange: (AttendanceFilter) -> Unit,
    onTeamSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Selector đội ở dòng riêng, full-width — nhãn "{Chiến dịch} - {Đội}" có
        // thể rất dài nên không đặt chung hàng với chip lọc để tránh đè nhau.
        if (state.showTeamSelector) {
            TeamSelector(state = state, onTeamSelect = onTeamSelect)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("Tất cả", state.filter == AttendanceFilter.ALL) {
                onFilterChange(AttendanceFilter.ALL)
            }
            FilterChip("Đã điểm danh", state.filter == AttendanceFilter.CHECKED_IN) {
                onFilterChange(AttendanceFilter.CHECKED_IN)
            }
            FilterChip("Chưa điểm danh", state.filter == AttendanceFilter.NOT_CHECKED_IN) {
                onFilterChange(AttendanceFilter.NOT_CHECKED_IN)
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) AttendanceTokens.Accent else AttendanceTokens.Surface,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, AttendanceTokens.Border),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) Color.White else AttendanceTokens.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TeamSelector(
    state: AttendanceUiState,
    onTeamSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = state.managedTeams
        .firstOrNull { it.teamId == state.selectedTeamId }?.displayLabel() ?: "Chọn đội"

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AttendanceTokens.Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AttendanceTokens.Border),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedName,
                color = AttendanceTokens.TextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = "Chọn đội",
                tint = AttendanceTokens.TextSecondary,
                modifier = Modifier.height(18.dp)
            )
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        state.managedTeams.forEach { team ->
            DropdownMenuItem(
                text = { Text(team.displayLabel()) },
                onClick = {
                    expanded = false
                    onTeamSelect(team.teamId)
                }
            )
        }
    }
}

/** Nhãn hiển thị "{Chiến dịch} - {Đội hình}"; bỏ phần chiến dịch nếu trống. */
private fun ManagedTeamUiModel.displayLabel(): String =
    if (campaignName.isNullOrBlank()) teamName else "$campaignName - $teamName"
