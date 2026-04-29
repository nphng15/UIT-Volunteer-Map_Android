package com.example.uitvolunteermap.core.network

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.error.toAppError
import com.example.uitvolunteermap.core.common.result.AppResult

suspend inline fun <T, R> apiCall(
    crossinline request: suspend () -> ApiEnvelope<T>,
    crossinline map: suspend (T) -> R
): AppResult<R> = try {
    val response = request()
    if (response.success && response.data != null) {
        AppResult.Success(map(response.data))
    } else {
        AppResult.Error(response.toAppError())
    }
} catch (throwable: Throwable) {
    AppResult.Error(throwable.toAppError())
}

/**
 * Biến thể cho các endpoint chỉ trả về `{ success, message }` (không có `data`),
 * điển hình là CREATE/DELETE. Chỉ xét cờ `success`, bỏ qua `data` (có thể null).
 */
suspend inline fun apiCallUnit(
    crossinline request: suspend () -> ApiEnvelope<*>
): AppResult<Unit> = try {
    val response = request()
    if (response.success) {
        AppResult.Success(Unit)
    } else {
        AppResult.Error(response.toAppError())
    }
} catch (throwable: Throwable) {
    AppResult.Error(throwable.toAppError())
}

/**
 * Biến thể cho endpoint trả về dữ liệu TRẦN (không bọc trong [ApiEnvelope]),
 * ví dụ `GET /accounts` trả thẳng một mảng JSON.
 */
suspend inline fun <T, R> apiCallRaw(
    crossinline request: suspend () -> T,
    crossinline map: suspend (T) -> R
): AppResult<R> = try {
    AppResult.Success(map(request()))
} catch (throwable: Throwable) {
    AppResult.Error(throwable.toAppError())
}

fun ApiEnvelope<*>.toAppError(): AppError {
    val detail = message ?: error ?: "Yêu cầu API không thành công."
    return when {
        error?.contains("token", ignoreCase = true) == true -> AppError.Unauthorized(detail)
        error?.contains("permission", ignoreCase = true) == true -> AppError.Forbidden(detail)
        error?.contains("validation", ignoreCase = true) == true -> AppError.Validation(detail)
        else -> AppError.Unknown(detail)
    }
}
