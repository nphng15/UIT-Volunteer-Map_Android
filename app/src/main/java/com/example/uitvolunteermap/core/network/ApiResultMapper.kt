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

fun ApiEnvelope<*>.toAppError(): AppError {
    val detail = message ?: error ?: "Yêu cầu API không thành công."
    return when {
        error?.contains("token", ignoreCase = true) == true -> AppError.Unauthorized(detail)
        error?.contains("permission", ignoreCase = true) == true -> AppError.Forbidden(detail)
        error?.contains("validation", ignoreCase = true) == true -> AppError.Validation(detail)
        else -> AppError.Unknown(detail)
    }
}
