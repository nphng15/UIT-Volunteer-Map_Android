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
        is AppError.Conflict -> localizeBusinessMessage(message)
        is AppError.Forbidden -> localizeBusinessMessage(message)
        is AppError.Network -> message
        is AppError.NotFound -> localizeBusinessMessage(message)
        is AppError.Server -> message
        is AppError.Unauthorized -> message
        is AppError.Unknown -> localizeBusinessMessage(message)
        is AppError.Validation -> localizeBusinessMessage(message)
    }

/**
 * Dịch các thông báo nghiệp vụ tiếng Anh từ backend (check-in & ảnh chiến dịch)
 * sang tiếng Việt tự nhiên. Khớp theo chuỗi con đã biết; lỗi không nhận diện được
 * giữ nguyên text gốc để không gộp các lỗi khác nhau thành một.
 */
private fun localizeBusinessMessage(raw: String): String {
    return when {
        // CHECKIN_ERRORS.ALREADY_CHECKED_IN
        raw.contains("already checked in to this campaign", ignoreCase = true) ->
            "Bạn đã điểm danh chiến dịch này hôm nay rồi."
        // CHECKIN_ERRORS.OUT_OF_RANGE
        raw.contains("not within the allowed check-in radius", ignoreCase = true) ->
            "Bạn đang ở ngoài bán kính cho phép điểm danh."
        // CHECKIN_ERRORS.CAMPAIGN_NOT_ACTIVE
        raw.contains("not currently active", ignoreCase = true) ->
            "Chiến dịch hiện chưa diễn ra hoặc đã kết thúc."
        // CHECKIN_ERRORS.CAMPAIGN_NO_LOCATION
        raw.contains("does not have a check-in location", ignoreCase = true) ->
            "Chiến dịch chưa được cấu hình vị trí điểm danh."
        // CAMPAIGN_PHOTO_ERRORS.NOT_CHECKED_IN
        raw.contains("must check in to this campaign", ignoreCase = true) ->
            "Bạn cần điểm danh chiến dịch này trước khi chia sẻ khoảnh khắc."
        else -> raw
    }
}
