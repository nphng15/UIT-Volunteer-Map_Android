package com.example.uitvolunteermap.features.team.presentation.addattachment

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.uitvolunteermap.core.ui.theme.ColorTokens
import com.example.uitvolunteermap.core.ui.theme.DesignTypography
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.core.ui.theme.Dimens
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttachmentScreen( // 1. Đổi tên từ AddAttachmentPopup thành AddAttachmentScreen
    uiState: AddAttachmentUiState,
    onEvent: (AddAttachmentUiEvent) -> Unit // 2. Chỉ nhận UI State và bắn Event ngược lại (Stateless)
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        onEvent(AddAttachmentUiEvent.OnPhotosPicked(uris)) // Chuyển logic sang Event
    }

    Dialog(
        onDismissRequest = { onEvent(AddAttachmentUiEvent.OnDismiss) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Thêm ảnh hoạt động",
                            style = DesignTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onEvent(AddAttachmentUiEvent.OnDismiss) }) {
                            Icon(Icons.Default.Close, null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ColorTokens.ScreenBackgroundTop
                    )
                )
            },
            containerColor = ColorTokens.ScreenBackgroundBottom
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Dimens.Spacing20),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20)
            ) {
                // Khung Upload
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(Shapes.Radius18))
                        .background(ColorTokens.ScreenBackgroundTop)
                        .clickable { launcher.launch("image/*") }
                        .border(1.dp, ColorTokens.BorderSubtle, RoundedCornerShape(Shapes.Radius18)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CloudUpload,
                            null,
                            tint = ColorTokens.BrandPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Nhấn để chọn ảnh", style = DesignTypography.bodyLarge)
                    }
                }

                // Preview Row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.temporaryAttachments) { item ->
                        Box(Modifier.size(100.dp).clip(RoundedCornerShape(Shapes.Radius18))) {
                            AsyncImage(
                                model = item.uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                            if (item.isLoading) {
                                CircularProgressIndicator(
                                    Modifier.align(Alignment.Center).size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Buttons
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { onEvent(AddAttachmentUiEvent.OnDismiss) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Hủy", color = Color.Black)
                    }
                    Button(
                        onClick = { onEvent(AddAttachmentUiEvent.OnSave) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorTokens.BrandPrimary),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Hoàn tất")
                        }
                    }
                }
            }
        }
    }
}

// Cập nhật Preview để khớp với tham số mới
@Preview(showBackground = true)
@Composable
fun PreviewAddAttachmentEmpty() {
    AddAttachmentScreen(
        uiState = AddAttachmentUiState(),
        onEvent = {}
    )
}