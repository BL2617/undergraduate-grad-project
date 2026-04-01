package com.bl2617.tamperrecovery.screens

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bl2617.tamperrecovery.ui.components.TechCard
import com.bl2617.tamperrecovery.ui.theme.Background
import com.bl2617.tamperrecovery.ui.theme.OnSurface
import com.bl2617.tamperrecovery.ui.theme.OnSurfaceVariant
import com.bl2617.tamperrecovery.ui.theme.Primary
import com.bl2617.tamperrecovery.ui.theme.PrimaryDark
import com.bl2617.tamperrecovery.ui.theme.Surface
import com.bl2617.tamperrecovery.ui.theme.SurfaceVariant

private val uploadImageCardWidthDp = 150
private val imageInfoHeightDp = 180
private val imageInfoWidthDp = 350
private val buttonWidthDp = 160
private val buttonHeightDp = 80
private val buttonRightPaddingDp = 60
private val uploadImageCardLeftPaddingDp = 40
/**
 * 检测功能主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
//    viewModel: DetectionViewModel,
    onStartDetection: (Uri) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageInfo by remember { mutableStateOf<ImageInfo?>(null) }

    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            // 获取图片信息
            val contentResolver: ContentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val fileSize = contentResolver.openInputStream(uri)?.available() ?: 0
            var fileName = "未知文件名"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }
            imageInfo = ImageInfo(
                fileName = fileName,
                fileType = mimeType ?: "未知类型",
                fileSize = fileSize
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "图像篡改检测",
                        color = OnSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        containerColor = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 图片选择区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((uploadImageCardWidthDp + 20).dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(uploadImageCardLeftPaddingDp.dp))
                TechCard(
                    modifier = Modifier
                        .width(uploadImageCardWidthDp.dp)
                        .height(uploadImageCardWidthDp.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Select image",
                                    modifier = Modifier.size(48.dp),
                                    tint = Primary
                                )
                                Text(
                                    text = "点击选择图片",
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "重新选择",
                    modifier = Modifier
                        .width(buttonWidthDp.dp)
                        .height(buttonHeightDp.dp)
                        .clickable(
                            selectedImageUri != null,
                            onClick = {
                                selectedImageUri = null
                                imageInfo = null
                            })
                        .background(color =  if (selectedImageUri != null) PrimaryDark else SurfaceVariant),
                )
                Spacer(modifier = Modifier.width(buttonRightPaddingDp.dp))

            }

            // 文件信息区域
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TechCard(
                    modifier = Modifier
                        .width(imageInfoWidthDp.dp)
                        .height(imageInfoHeightDp.dp)
                ) {
                    Spacer(modifier = Modifier.width(uploadImageCardLeftPaddingDp.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "文件名: ${imageInfo?.fileName ?: ""}",
                            color = OnSurface
                        )
                        Text(
                            text = "文件类型: ${imageInfo?.fileType ?: ""}",
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "文件大小: ${formatFileSize(imageInfo?.fileSize ?: 0)}",
                            color = OnSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "开始检测",
                    modifier = Modifier
                        .width(buttonWidthDp.dp)
                        .height(buttonHeightDp.dp)
                        .clickable(
                            selectedImageUri != null,
                            onClick = {
                                selectedImageUri?.let { uri ->
                                    onStartDetection(uri)
                                }
                            })
                        .background(color = if (selectedImageUri != null) PrimaryDark else SurfaceVariant),
                )
                Spacer(modifier = Modifier.width(buttonRightPaddingDp.dp))
            }
        }
    }
}

/**
 * 图片信息数据类
 */
data class ImageInfo(
    val fileName: String,
    val fileType: String,
    val fileSize: Int
)

/**
 * 格式化文件大小
 */
fun formatFileSize(bytes: Int): String {
    return when {
        bytes < 1024 -> "${bytes}B"
        bytes < 1024 * 1024 -> "${String.format("%.2f", bytes / 1024.0)}KB"
        else -> "${String.format("%.2f", bytes / (1024.0 * 1024.0))}MB"
    }
}

@Preview(widthDp = 1400, heightDp = 700)
@Composable
fun DetectionScreenPreview() {
    DetectionScreen {

    }
}