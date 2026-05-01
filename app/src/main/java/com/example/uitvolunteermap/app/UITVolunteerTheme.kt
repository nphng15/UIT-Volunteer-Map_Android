package com.example.uitvolunteermap.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.uitvolunteermap.core.ui.theme.UITDesignColorScheme
import com.example.uitvolunteermap.core.ui.theme.UITDesignTypography

@Composable
fun UITVolunteerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) UITDesignColorScheme.Dark else UITDesignColorScheme.Light,
        typography = UITDesignTypography,
        content = content
    )
}
