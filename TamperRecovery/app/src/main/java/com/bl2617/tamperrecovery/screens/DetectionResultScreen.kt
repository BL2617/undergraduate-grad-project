package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bl2617.tamperrecovery.data.model.DetectionResultData
import com.bl2617.tamperrecovery.viewmodel.DetectionViewModel
import com.bl2617.tamperrecovery.viewmodel.VisualizationState

/**
 * 检测结果展示界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionResultScreen(
    result: DetectionResultData,
    viewModel: DetectionViewModel,
    onBack: () -> Unit
) {
    val visualizationState by viewModel.visualizationState.collectAsStateWithLifecycle()
    
    // 加载可视化图片
    LaunchedEffect(result.id) {
        if (result.visualizationUrl != null) {
            viewModel.loadVisualization(result.id)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("检测结果") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 检测结果摘要卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.isTampered) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (result.isTampered) "检测到篡改" else "未检测到篡改",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (result.isTampered) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    
                    if (result.tamperRatioPercent != null) {
                        Text(
                            text = "篡改比例: ${String.format("%.2f", result.tamperRatioPercent)}%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    if (result.confidence != null) {
                        Text(
                            text = "置信度: ${result.confidence}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // 可视化图片
            if (result.visualizationUrl != null) {
                Text(
                    text = "可视化结果",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                when (val state = visualizationState) {
                    is VisualizationState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is VisualizationState.Success -> {
                        androidx.compose.foundation.Image(
                            bitmap = state.bitmap.asImageBitmap(),
                            contentDescription = "可视化结果",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    is VisualizationState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "加载可视化图片失败: ${state.message}",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    else -> {}
                }
            }
            
            // 篡改区域列表
            if (result.tamperedRegions != null && result.tamperedRegions.isNotEmpty()) {
                Text(
                    text = "篡改区域",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                result.tamperedRegions.forEachIndexed { index, region ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "区域 ${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Text("位置: (${region.x}, ${region.y})")
                            Text("尺寸: ${region.width} × ${region.height}")
                            if (region.confidence != null) {
                                Text("置信度: ${String.format("%.2f", region.confidence)}")
                            }
                        }
                    }
                }
            }
            
            // 检测信息
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "检测信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text("检测类型: ${result.detectionType}")
                    Text("检测结果ID: ${result.id}")
                    if (result.originalImageId != null) {
                        Text("原图ID: ${result.originalImageId}")
                    }
                    if (result.createdAt != null) {
                        Text("检测时间: ${result.createdAt}")
                    }
                }
            }
        }
    }
}

