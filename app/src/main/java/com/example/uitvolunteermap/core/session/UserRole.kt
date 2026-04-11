package com.example.uitvolunteermap.core.session

enum class UserRole {
    /** Chưa đăng nhập — chỉ xem, không có quyền ghi */
    GUEST,

    /** Đã đăng nhập — đầy đủ quyền tạo/sửa/xóa/đăng bài */
    VOLUNTEER
}
