package com.example.uitvolunteermap.features.auth.presentation

sealed interface LoginUiEvent {
    /** Đăng nhập thành công; [isAdmin] quyết định điều hướng vào khu quản trị hay màn chính. */
    data class NavigateToHome(val isAdmin: Boolean) : LoginUiEvent
}
