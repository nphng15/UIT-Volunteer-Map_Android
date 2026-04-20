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

    // ── Mock default: GUEST ──────────────────────────────────────────────────
    // Đổi thành UserRole.VOLUNTEER để xem UI đầy đủ quyền
    private val _userRole = MutableStateFlow<UserRole>(UserRole.GUEST)

    /** Role hiện tại dưới dạng cold StateFlow — các ViewModel có thể collect nếu cần reactive */
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    /** Snapshot nhanh — đủ dùng khi chỉ cần đọc 1 lần tại thời điểm load */
    val isGuest: Boolean get() = _userRole.value == UserRole.GUEST

    // ── Real: gọi từ AuthViewModel sau khi login thành công ─────────────────
    fun setRole(role: UserRole) {
        _userRole.value = role
    }

    // ── Real: gọi khi logout ─────────────────────────────────────────────────
    fun clearSession() {
        _userRole.value = UserRole.GUEST
    }
}
