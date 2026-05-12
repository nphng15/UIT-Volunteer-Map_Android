package com.example.uitvolunteermap.features.admin.account.presentation

sealed interface AdminAccountUiEffect {
    data class ShowMessage(val message: String) : AdminAccountUiEffect
}
