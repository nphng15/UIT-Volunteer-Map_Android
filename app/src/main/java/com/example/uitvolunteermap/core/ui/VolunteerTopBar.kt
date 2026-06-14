package com.example.uitvolunteermap.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette

/**
 * Thanh điều hướng dùng chung cho các màn phía tình nguyện viên.
 *
 * Một API duy nhất phục vụ cả hai kiểu bố cục đang tồn tại trong app:
 *  - Kiểu "chi tiết": nút quay lại + nhãn [eyebrow] căn giữa + ô hành động phải tuỳ chọn
 *    (CampaignDetail, TeamFormationDetail, CampaignPosts).
 *  - Kiểu "tiêu đề": nút quay lại (tuỳ chọn) + [title] căn trái + ô hành động phải
 *    (Profile, CampaignList).
 *
 * Nút quay lại thống nhất dùng [Icons.AutoMirrored.Filled.ArrowBack] để bảo đảm a11y.
 * Truyền `onBack = null` cho các tab gốc (không có hành vi quay lại).
 */
@Composable
fun VolunteerTopBar(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backContentDescription: String = "Quay lại",
    title: String? = null,
    titleColor: Color = VolunteerFlowPalette.TextPrimary,
    center: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Leading: nút quay lại hoặc ô giữ chỗ để cân đối phần center căn giữa.
        if (onBack != null) {
            VolunteerTopBarBackButton(
                contentDescription = backContentDescription,
                onClick = onBack
            )
        } else if (center != null) {
            Box(modifier = Modifier.size(40.dp))
        }

        if (center != null) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                center()
            }
        } else {
            if (title != null) {
                Text(
                    text = title,
                    color = titleColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (onBack != null) 12.dp else 0.dp)
                )
            } else {
                Box(modifier = Modifier.weight(1f))
            }
        }

        // Trailing: hành động phải, tối thiểu rộng 40.dp để cân đối với nút quay lại.
        Box(
            modifier = Modifier.defaultMinSize(minWidth = 40.dp, minHeight = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            trailing()
        }
    }
}

@Composable
private fun VolunteerTopBarBackButton(
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(VolunteerFlowPalette.Surface)
            .border(1.dp, VolunteerFlowPalette.Border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = contentDescription,
            tint = VolunteerFlowPalette.TextPrimary
        )
    }
}
