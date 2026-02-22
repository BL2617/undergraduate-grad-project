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
import com.bl2617.tamperrecovery.viewmodel.DetectionState
import com.bl2617.tamperrecovery.viewmodel.DetectionViewModel
import java.io.File

/**
 * LSB检测界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LSBDetectionScreen(
    viewModel: DetectionViewModel,
    onBack: () -> Unit,
    onResult: (com.bl2617.tamperrecovery.data.model.DetectionResultData) -> Unit
) {
    val detectionState by viewModel.lsbDetectionState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var key by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    // 进入页面时清理上一次检测状态
    LaunchedEffect(Unit) {
        viewModel.clearLsbState()
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
                title = { Text("LSB水印检测") },
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
            // 密钥输入
            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("用户密钥（可选）") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 选择图片按钮
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedFile != null) "重新选择图片" else "选择图片")
            }
            
            // 显示选中的图片文件名
            if (selectedFile != null) {
                Text(
                    text = "已选择: ${selectedFile!!.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // 开始检测按钮
            Button(
                onClick = {
                    if (selectedFile != null) {
                        viewModel.detectLSB(selectedFile!!, key)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedFile != null && detectionState !is DetectionState.Loading
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
}

