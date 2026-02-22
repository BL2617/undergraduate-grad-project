package com.bl2617.tamperrecovery.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.viewmodel.DetectionState
import com.bl2617.tamperrecovery.viewmodel.DetectionViewModel
import java.io.File

/**
 * 分块比对检测界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareDetectionScreen(
    viewModel: DetectionViewModel,
    onBack: () -> Unit,
    onResult: (com.bl2617.tamperrecovery.data.model.DetectionResultData) -> Unit
) {
    val detectionState by viewModel.compareDetectionState.collectAsStateWithLifecycle()
    val imageList by viewModel.imageListForSelection.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var selectedOriginalImage by remember { mutableStateOf<ImageData?>(null) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var threshold by remember { mutableStateOf("0.1") }
    var showImageSelector by remember { mutableStateOf(false) }
    
    // 进入页面时清理上一次检测状态，并加载图片列表
    LaunchedEffect(Unit) {
        viewModel.clearCompareState()
        viewModel.loadImageListForSelection()
    }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.cacheDir, "detect_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }
                inputStream?.close()
                selectedFile = file
            } catch (e: Exception) {
                // 错误处理
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分块比对检测") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 选择原图
            OutlinedTextField(
                value = selectedOriginalImage?.id ?: "",
                onValueChange = {},
                label = { Text("原图ID") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { showImageSelector = true }) {
                        Text("选择")
                    }
                }
            )
            
            if (selectedOriginalImage != null) {
                Text(
                    text = "已选择原图: ${selectedOriginalImage!!.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // 选择待检测图片
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedFile != null) "重新选择待检测图片" else "选择待检测图片")
            }
            
            if (selectedFile != null) {
                Text(
                    text = "已选择: ${selectedFile!!.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // 阈值
            OutlinedTextField(
                value = threshold,
                onValueChange = { threshold = it },
                label = { Text("差异阈值") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 开始检测按钮
            Button(
                onClick = {
                    if (selectedOriginalImage != null && selectedFile != null) {
                        val thresholdFloat = threshold.toFloatOrNull() ?: 0.1f
                        viewModel.detectCompare(
                            selectedOriginalImage!!.id!!,
                            selectedFile!!,
                            threshold = thresholdFloat
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedOriginalImage != null && selectedFile != null && 
                         detectionState !is DetectionState.Loading
            ) {
                if (detectionState is DetectionState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("检测中...")
                } else {
                    Text("开始检测")
                }
            }
            
            // 检测结果
            when (val state = detectionState) {
                is DetectionState.Success -> {
                    // 使用检测结果ID作为key，确保只调用一次 onResult
                    LaunchedEffect(state.result.id) {
                        onResult(state.result)
                    }
                }
                is DetectionState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "检测失败: ${state.message}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    // 图片选择对话框
    if (showImageSelector) {
        AlertDialog(
            onDismissRequest = { showImageSelector = false },
            title = { Text("选择原图") },
            text = {
                Column {
                    if (imageList.isEmpty()) {
                        Text("暂无图片，请先上传图片")
                    } else {
                        imageList.forEach { image ->
                            TextButton(
                                onClick = {
                                    selectedOriginalImage = image
                                    showImageSelector = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${image.id} (${image.width}×${image.height})")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageSelector = false }) {
                    Text("取消")
                }
            }
        )
    }
}

