package com.example.uitvolunteermap.core.session

enum class UserRole {
    /** Chưa đăng nhập — chỉ xem, không có quyền ghi */
    GUEST,

    /** Tài khoản admin từ backend Node.js. */
    ADMIN,

    /** Tài khoản leader từ backend Node.js. */
    LEADER,

    /** Alias cũ cho các test/flow volunteer đã có trước khi nối backend. */
    VOLUNTEER
}
