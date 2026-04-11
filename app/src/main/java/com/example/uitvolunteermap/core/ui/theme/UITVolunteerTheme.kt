package com.example.uitvolunteermap.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun UITVolunteerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DesignColorScheme.Dark else DesignColorScheme.Light,
        typography = DesignTypography,
        content = content
    )
}
