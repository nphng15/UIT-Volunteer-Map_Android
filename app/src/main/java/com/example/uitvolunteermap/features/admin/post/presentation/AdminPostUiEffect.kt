package com.example.uitvolunteermap.features.admin.post.presentation

sealed interface AdminPostUiEffect {
    data class ShowMessage(val message: String) : AdminPostUiEffect
}
