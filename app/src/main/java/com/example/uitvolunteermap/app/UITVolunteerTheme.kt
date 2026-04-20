package com.example.uitvolunteermap.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.uitvolunteermap.core.ui.theme.DesignColorScheme
import com.example.uitvolunteermap.core.ui.theme.DesignTypography

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
