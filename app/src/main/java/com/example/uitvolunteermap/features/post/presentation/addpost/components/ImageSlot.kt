package com.example.uitvolunteermap.features.post.presentation.addpost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Shapes

@Composable
internal fun ImageSlot(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(Shapes.Radius18))
            .background(if (selected) PopupAccentSurface else PopupInput)
            .border(
                1.dp,
                if (selected) PopupAccent.copy(alpha = 0.14f) else PopupAccent.copy(alpha = 0.10f),
                RoundedCornerShape(Shapes.Radius18)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) PopupPrimary else PopupSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
