package com.example.uitvolunteermap.features.attendance.presentation

data class AttendanceUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val teamName: String = "",
    val campaignName: String? = null,
    val date: String = "",
    /** Ngày đang lọc (yyyy-MM-dd); null = hôm nay. Gửi xuống API qua ?date=. */
    val selectedDate: String? = null,
    val managedTeams: List<ManagedTeamUiModel> = emptyList(),
    val selectedTeamId: Int? = null,
    val members: List<MemberAttendanceUiModel> = emptyList(),
    val filter: AttendanceFilter = AttendanceFilter.ALL,
    val previewMember: MemberAttendanceUiModel? = null
) {
    val checkedInCount: Int get() = members.count { it.hasCheckedIn }
    val notCheckedInCount: Int get() = members.size - checkedInCount
    val totalCount: Int get() = members.size
    val progress: Float get() = if (totalCount == 0) 0f else checkedInCount.toFloat() / totalCount

    val visibleMembers: List<MemberAttendanceUiModel>
        get() = when (filter) {
            AttendanceFilter.ALL -> members
            AttendanceFilter.CHECKED_IN -> members.filter { it.hasCheckedIn }
            AttendanceFilter.NOT_CHECKED_IN -> members.filter { !it.hasCheckedIn }
        }

    val showTeamSelector: Boolean get() = managedTeams.size > 1
}

enum class AttendanceFilter { ALL, CHECKED_IN, NOT_CHECKED_IN }

data class ManagedTeamUiModel(
    val teamId: Int,
    val teamName: String,
    val campaignName: String?
)

data class MemberAttendanceUiModel(
    val userId: Int,
    val fullName: String,
    val mssv: String,
    val initials: String,
    val hasCheckedIn: Boolean,
    val checkedInAt: String?,
    val distanceMeters: Double?,
    val imageUrl: String?
) {
    /** Đã điểm danh nhưng cách xa bất thường (>300m) — nghi điểm danh hộ. */
    val isSuspicious: Boolean get() = hasCheckedIn && (distanceMeters ?: 0.0) > 300
    /** Chưa điểm danh và đang ở rất xa (>500m). */
    val isTooFar: Boolean get() = !hasCheckedIn && (distanceMeters ?: 0.0) > 500
}
