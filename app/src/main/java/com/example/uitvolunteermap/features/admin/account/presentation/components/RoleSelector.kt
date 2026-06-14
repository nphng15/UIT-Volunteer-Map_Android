package com.example.uitvolunteermap.features.admin.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.admin.account.presentation.AccountRole

/** Bộ chọn vai trò dạng pill (dùng trong form tạo & sửa). */
@Composable
internal fun RoleSelector(
    selected: AccountRole,
    onSelected: (AccountRole) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Vai trò",
            color = AccountTokens.SecondaryText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AccountRole.entries.forEach { role ->
                val isSelected = role == selected
                Text(
                    text = role.label,
                    color = if (isSelected) AccountTokens.Surface else AccountTokens.SecondaryText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) AccountTokens.Accent else AccountTokens.Surface
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AccountTokens.Accent else AccountTokens.Border,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = enabled) { onSelected(role) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
