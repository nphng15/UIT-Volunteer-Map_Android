package com.example.uitvolunteermap.core.common.error

sealed interface AppError {
    data class Network(
        val message: String = "Unable to connect to server.",
    ) : AppError

    data class Validation(
        val message: String,
    ) : AppError

    data class Unauthorized(
        val message: String = "Your session has expired.",
    ) : AppError

    data class Forbidden(
        val message: String = "You do not have permission for this action.",
    ) : AppError

    data class NotFound(
        val message: String = "Requested resource was not found.",
    ) : AppError

    data class Server(
        val code: Int,
        val message: String = "Server is temporarily unavailable.",
    ) : AppError

    data class Unknown(
        val message: String = "Something went wrong.",
    ) : AppError
}

val AppError.userMessage: String
    get() = when (this) {
        is AppError.Forbidden -> message
        is AppError.Network -> message
        is AppError.NotFound -> message
        is AppError.Server -> message
        is AppError.Unauthorized -> message
        is AppError.Unknown -> message
        is AppError.Validation -> message
    }
