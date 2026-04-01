package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bl2617.tamperrecovery.data.model.DetectionResultData
import com.bl2617.tamperrecovery.ui.components.*
import com.bl2617.tamperrecovery.ui.theme.*
import com.bl2617.tamperrecovery.viewmodel.DetectionViewModel
import com.bl2617.tamperrecovery.viewmodel.DetectionHistoryState

/**
 * 检测历史界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionHistoryScreen(
    viewModel: DetectionViewModel,
    onBack: () -> Unit,
    onViewResult: (DetectionResultData) -> Unit
) {
    val historyState by viewModel.detectionHistoryState.collectAsStateWithLifecycle()

    // 加载检测历史
    LaunchedEffect(Unit) {
        viewModel.loadDetectionHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "检测历史",
                        color = OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Primary
                        )
                    }
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
                .padding(16.dp)
        ) {
            when (val state = historyState) {
                is DetectionHistoryState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is DetectionHistoryState.Success -> {
                    if (state.results.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TechSubtitle(
                                    text = "暂无检测历史",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                TechSubtitle(
                                    text = "进行检测后，结果会显示在这里"
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.results) {
                                HistoryItem(
                                    result = it,
                                    onClick = {
                                        onViewResult(it)
                                    }
                                )
                            }
                        }
                    }
                }
                is DetectionHistoryState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "加载检测历史失败",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Error
                            )
                            TechButton(
                                text = "重试",
                                onClick = {
                                    viewModel.loadDetectionHistory()
                                }
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

/**
 * 历史记录项
 */
@Composable
fun HistoryItem(
    result: DetectionResultData,
    onClick: () -> Unit
) {
    TechCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (result.detectionType) {
                        "lsb" -> "LSB水印检测"
                        "model" -> "模型检测"
                        "compare" -> "分块比对检测"
                        else -> "未知检测类型"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    text = if (result.isTampered) "有篡改" else "未篡改",
                    color = if (result.isTampered) Error else Success
                )
            }

            if (result.tamperRatioPercent != null) {
                Text(
                    text = "篡改比例: ${String.format("%.2f", result.tamperRatioPercent)}%",
                    color = OnSurfaceVariant
                )
            }

            if (result.confidence != null) {
                Text(
                    text = "置信度: ${result.confidence}",
                    color = OnSurfaceVariant
                )
            }

            Text(
                text = "检测时间: ${result.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}