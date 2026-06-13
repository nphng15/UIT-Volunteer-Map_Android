package com.example.uitvolunteermap.features.home.presentation.volunteer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenAccent
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSecondary

@Composable
internal fun VolunteerBackdrop() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 44.dp)
                .size(184.dp)
                .background(ScreenPrimary.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-34).dp)
                .size(112.dp)
                .background(ScreenSecondary.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 22.dp)
                .size(88.dp)
                .background(ScreenAccent.copy(alpha = 0.08f), CircleShape)
        )
    }
}
