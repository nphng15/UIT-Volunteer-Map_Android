package com.example.uitvolunteermap.core.common.error

import java.io.IOException
import retrofit2.HttpException

fun Throwable.toAppError(): AppError = when (this) {
    is IOException -> AppError.Network()
    is HttpException -> when (code()) {
        401 -> AppError.Unauthorized()
        403 -> AppError.Forbidden()
        404 -> AppError.NotFound()
        in 500..599 -> AppError.Server(code = code())
        else -> AppError.Unknown(message = message())
    }

    else -> AppError.Unknown(message = message ?: "Something went wrong.")
}
