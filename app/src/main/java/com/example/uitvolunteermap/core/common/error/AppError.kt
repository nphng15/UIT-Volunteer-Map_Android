package com.example.uitvolunteermap.core.common.error

sealed interface AppError {
    data class Network(
        val message: String = "Không thể kết nối đến máy chủ.",
    ) : AppError

    data class Validation(
        val message: String,
    ) : AppError

    data class Unauthorized(
        val message: String = "Phiên đăng nhập đã hết hạn.",
    ) : AppError

    data class Forbidden(
        val message: String = "Bạn không có quyền thực hiện thao tác này.",
    ) : AppError

    data class NotFound(
        val message: String = "Không tìm thấy dữ liệu yêu cầu.",
    ) : AppError

    // HTTP 409 — tên chiến dịch / tài nguyên đã tồn tại
    data class Conflict(
        val message: String = "Tài nguyên với tên này đã tồn tại.",
    ) : AppError

    data class Server(
        val code: Int,
        val message: String = "Máy chủ đang tạm thời không khả dụng.",
    ) : AppError

    data class Unknown(
        val message: String = "Đã xảy ra lỗi không xác định.",
    ) : AppError
}

val AppError.userMessage: String
    get() = when (this) {
        is AppError.Conflict -> message
        is AppError.Forbidden -> message
        is AppError.Network -> message
        is AppError.NotFound -> message
        is AppError.Server -> message
        is AppError.Unauthorized -> message
        is AppError.Unknown -> message
        is AppError.Validation -> message
    }
