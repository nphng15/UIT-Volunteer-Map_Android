package com.example.uitvolunteermap.core.session

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton giữ trạng thái phiên đăng nhập hiện tại.
 *
 * Mock: đổi giá trị khởi tạo để test UI từng role —
 *   UserRole.GUEST     → ẩn tất cả nút ghi (delete, edit ảnh, add activity)
 *   UserRole.VOLUNTEER → hiện đầy đủ
 *
 * Real: gọi [setRole] sau khi login API trả về token + role;
 *       gọi [clearSession] khi logout.
 *       Lưu role xuống DataStore/SharedPreferences để persist qua lần mở app.
 */
@Singleton
class SessionManager @Inject constructor() {

    private companion object {
        const val MockVolunteerUserId = 20
    }

    // ── Mock default: GUEST ──────────────────────────────────────────────────
    // Đổi thành UserRole.VOLUNTEER để xem UI đầy đủ quyền
    private val _userRole = MutableStateFlow<UserRole>(UserRole.GUEST)
    private val _accessToken = MutableStateFlow<String?>(null)
    private val _accountId = MutableStateFlow<Int?>(null)
    private val _username = MutableStateFlow<String?>(null)

    /** Role hiện tại dưới dạng cold StateFlow — các ViewModel có thể collect nếu cần reactive */
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    /** Snapshot nhanh — đủ dùng khi chỉ cần đọc 1 lần tại thời điểm load */
    val isGuest: Boolean get() = _userRole.value == UserRole.GUEST
    val canManagePosts: Boolean get() = _userRole.value in setOf(UserRole.ADMIN, UserRole.LEADER, UserRole.VOLUNTEER)
    val currentUserId: Int
        get() = _accountId.value ?: if (isGuest) 0 else MockVolunteerUserId
    val currentUsername: String?
        get() = _username.value
    val bearerToken: String?
        get() = _accessToken.value?.let { "Bearer $it" }

    // ── Real: gọi từ AuthViewModel sau khi login thành công ─────────────────
    fun setRole(role: UserRole) {
        _userRole.value = role
    }

    fun setAuthenticatedSession(
        token: String,
        accountId: Int,
        username: String,
        role: UserRole
    ) {
        _accessToken.value = token
        _accountId.value = accountId
        _username.value = username
        _userRole.value = role
    }

    // ── Real: gọi khi logout ─────────────────────────────────────────────────
    fun clearSession() {
        _accessToken.value = null
        _accountId.value = null
        _username.value = null
        _userRole.value = UserRole.GUEST
    }
}
