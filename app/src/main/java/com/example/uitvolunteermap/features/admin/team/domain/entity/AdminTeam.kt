package com.example.uitvolunteermap.features.admin.team.domain.entity

/**
 * Mô hình domain cho một đội (team) ở khu quản trị.
 *
 * Lưu ý hợp đồng API: danh sách GET /teams (khi đã đăng nhập) trả về members,
 * còn GET /teams/{id} thì KHÔNG. Khi dựng từ danh sách, [memberCount] phản ánh số
 * thành viên thực; khi dựng từ chi tiết, members rỗng nên memberCount = 0.
 */
data class AdminTeam(
    val teamId: Int,
    val teamName: String,
    val description: String?,
    val imageUrl: String?,
    val checkInLatitude: Double?,
    val checkInLongitude: Double?,
    val checkInRadius: Double?,
    val leaders: List<AdminTeamLeader>,
    val members: List<AdminTeamMember>
) {
    val memberCount: Int get() = members.size
    val isCheckInConfigured: Boolean
        get() = checkInLatitude != null && checkInLongitude != null
}

data class AdminTeamLeader(
    val userId: Int,
    val fullName: String,
    val role: String?,
    val avatarUrl: String?
)

data class AdminTeamMember(
    val userId: Int,
    val fullName: String
)
