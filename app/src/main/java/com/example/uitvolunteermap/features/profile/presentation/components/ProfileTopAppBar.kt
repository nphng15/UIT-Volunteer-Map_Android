package com.example.uitvolunteermap.features.profile.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource // Thêm import này
import com.example.uitvolunteermap.R // Import R của dự án
import com.example.uitvolunteermap.core.ui.theme.FontTokens // Dùng FontTokens thống nhất

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.profile_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontTokens.Heading // Sử dụng FontTokens đồng bộ
                ),
                maxLines = 1,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    // SỬA: ContentDescription cũng nên dùng stringResource cho Accessibility
                    contentDescription = stringResource(id = R.string.back_button_description)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // Giữ nguyên việc dùng MaterialTheme để hỗ trợ DarkMode tự động
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}