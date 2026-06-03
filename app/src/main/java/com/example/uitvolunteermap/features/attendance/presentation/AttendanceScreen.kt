package com.example.uitvolunteermap.features.attendance.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.features.attendance.presentation.components.AttendanceContextCard
import com.example.uitvolunteermap.features.attendance.presentation.components.AttendanceFilterRow
import com.example.uitvolunteermap.features.attendance.presentation.components.AttendanceTokens
import com.example.uitvolunteermap.features.attendance.presentation.components.MemberAttendanceCard
import com.example.uitvolunteermap.features.attendance.presentation.components.PhotoProofDialog

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    state: AttendanceUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AttendanceUiEvent) -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.AttendanceScreen),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AttendanceTokens.ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.selectedDate == null) "Điểm danh hôm nay" else "Điểm danh ${state.date}",
                        fontWeight = FontWeight.Bold,
                        color = AttendanceTokens.TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Chọn ngày",
                            tint = AttendanceTokens.TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AttendanceTokens.Surface
                )
            )
        },
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Manage,
                onTabSelected = { selectedTab ->
                    if (selectedTab != VolunteerBottomBarTab.Manage) {
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
        ) {
            when {
                state.isLoading && state.members.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.members.isEmpty() -> {
                    Text(
                        text = state.errorMessage,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        color = AttendanceTokens.TextSecondary
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(VolunteerFlowTestTags.AttendanceList),
                        contentPadding = PaddingValues(AttendanceTokens.ScreenPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AttendanceContextCard(state = state)
                        }
                        item {
                            AttendanceFilterRow(
                                state = state,
                                onFilterChange = { onEvent(AttendanceUiEvent.FilterChanged(it)) },
                                onTeamSelect = { onEvent(AttendanceUiEvent.TeamSelected(it)) }
                            )
                        }
                        items(state.visibleMembers, key = { it.userId }) { member ->
                            MemberAttendanceCard(
                                member = member,
                                onPhotoTap = { onEvent(AttendanceUiEvent.MemberPhotoTapped(it)) },
                                modifier = Modifier.testTag(
                                    VolunteerFlowTestTags.attendanceMemberCard(member.userId)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    state.previewMember?.let { member ->
        PhotoProofDialog(
            member = member,
            onDismiss = { onEvent(AttendanceUiEvent.PhotoPreviewDismissed) }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onEvent(AttendanceUiEvent.DateSelected(millis.toIsoDate()))
                    }
                    showDatePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Huỷ") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/** epoch millis (UTC) → "yyyy-MM-dd", khớp định dạng ngày backend dùng. */
private fun Long.toIsoDate(): String {
    val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    format.timeZone = java.util.TimeZone.getTimeZone("UTC")
    return format.format(java.util.Date(this))
}
