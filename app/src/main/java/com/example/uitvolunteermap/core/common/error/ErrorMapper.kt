package com.example.uitvolunteermap.core.common.error

import java.io.IOException
import org.json.JSONObject
import retrofit2.HttpException

fun Throwable.toAppError(): AppError = when (this) {
    is IOException -> AppError.Network()
    is HttpException -> {
        // Đọc message nghiệp vụ từ body lỗi của backend ({success:false, error/message}).
        val backendMessage = parseBackendErrorMessage(this)
        when (code()) {
            401 -> if (backendMessage != null) AppError.Unauthorized(backendMessage) else AppError.Unauthorized()
            403 -> if (backendMessage != null) AppError.Forbidden(backendMessage) else AppError.Forbidden()
            404 -> if (backendMessage != null) AppError.NotFound(backendMessage) else AppError.NotFound()
            409 -> if (backendMessage != null) AppError.Conflict(backendMessage) else AppError.Conflict()
            in 400..499 -> AppError.Validation(backendMessage ?: (message ?: "Yêu cầu không hợp lệ."))
            in 500..599 -> AppError.Server(code = code())
            else -> AppError.Unknown(message = backendMessage ?: message())
        }
    }

    else -> AppError.Unknown(message = message ?: "Đã xảy ra lỗi không xác định.")
}

/** Trích chuỗi "error"/"message" trong body lỗi JSON của backend, nếu có. */
private fun parseBackendErrorMessage(exception: HttpException): String? {
    return try {
        val raw = exception.response()?.errorBody()?.string()?.takeIf { it.isNotBlank() } ?: return null
        val json = JSONObject(raw)
        val value = json.optString("error").ifBlank { json.optString("message") }
        value.takeIf { it.isNotBlank() }
    } catch (_: Throwable) {
        null
    }
}
