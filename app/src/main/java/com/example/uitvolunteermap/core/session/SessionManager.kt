package com.example.uitvolunteermap.core.session

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManager @Inject constructor(
    private val sessionStorage: SessionStorage
) {

    private val _userRole = MutableStateFlow(UserRole.GUEST)
    private val _accessToken = MutableStateFlow<String?>(null)
    private val _accountId = MutableStateFlow<Int?>(null)
    private val _username = MutableStateFlow<String?>(null)

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    val isGuest: Boolean get() = _userRole.value == UserRole.GUEST
    val canManagePosts: Boolean get() = _userRole.value in setOf(UserRole.ADMIN, UserRole.LEADER)

    /** Trưởng nhóm: chỉ leader mới thấy khu quản lý điểm danh đội. */
    val isLeader: Boolean get() = _userRole.value == UserRole.LEADER

    /** Quản lý chiến dịch (tạo/sửa/xoá) chỉ dành cho admin; trưởng nhóm không có quyền này. */
    val canManageCampaigns: Boolean get() = _userRole.value == UserRole.ADMIN

    /** Điểm danh GPS là hành động thực địa của tình nguyện viên và trưởng nhóm, không dành cho admin. */
    val canCheckin: Boolean get() = _userRole.value in setOf(UserRole.VOLUNTEER, UserRole.LEADER)
    val currentUserId: Int
        get() = _accountId.value ?: 0
    val currentUsername: String?
        get() = _username.value
    val bearerToken: String?
        get() = _accessToken.value?.let { "Bearer $it" }

    init {
        sessionStorage.load()?.let { saved ->
            _accessToken.value = saved.token
            _accountId.value = saved.accountId
            _username.value = saved.username
            _userRole.value = saved.role
        }
    }

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
        sessionStorage.save(token, accountId, username, role)
    }

    fun clearSession() {
        _accessToken.value = null
        _accountId.value = null
        _username.value = null
        _userRole.value = UserRole.GUEST
        sessionStorage.clear()
    }

    fun onSessionExpired() {
        if (_accessToken.value != null) {
            clearSession()
            _sessionExpiredEvent.tryEmit(Unit)
        }
    }
}
