package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.account.presentation.AccountRole

/** Thanh tìm kiếm theo username + chip lọc theo vai trò. */
@Composable
internal fun AccountSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    roleFilter: AccountRole?,
    onRoleFilterChange: (AccountRole?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = AccountTokens.MutedText
                )
            },
            placeholder = {
                Text(text = "Tìm theo tên đăng nhập…", color = AccountTokens.MutedText)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AccountTokens.Surface,
                unfocusedContainerColor = AccountTokens.Surface,
                focusedBorderColor = AccountTokens.Accent,
                unfocusedBorderColor = AccountTokens.Border,
                focusedTextColor = AccountTokens.PrimaryText,
                unfocusedTextColor = AccountTokens.PrimaryText
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(label = "Tất cả", selected = roleFilter == null) {
                onRoleFilterChange(null)
            }
            AccountRole.entries.forEach { role ->
                FilterChip(label = role.label, selected = roleFilter == role) {
                    onRoleFilterChange(role)
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        color = if (selected) AccountTokens.Surface else AccountTokens.SecondaryText,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AccountTokens.PrimaryText else AccountTokens.Surface)
            .border(
                width = 1.dp,
                color = if (selected) AccountTokens.PrimaryText else AccountTokens.Border,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}
