package com.example.uitvolunteermap.core.common.result

import com.example.uitvolunteermap.core.common.error.AppError

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}
