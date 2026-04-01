package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bl2617.tamperrecovery.data.model.DetectionResultData
import com.bl2617.tamperrecovery.ui.components.*
import com.bl2617.tamperrecovery.ui.theme.*
import com.bl2617.tamperrecovery.viewmodel.DetectionViewModel
import com.bl2617.tamperrecovery.viewmodel.VisualizationState
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.foundation.background

/**
 * 检测结果展示界面
 */
@Composable
fun DetectionResultScreen(
    result: DetectionResultData,
    originalImageUri: Uri?,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 返回按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TechButton(
                text = "返回",
                onClick = onBack,
                modifier = Modifier.width(100.dp)
            )
        }

        // 原图片和检测结果区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // 原图片
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TechCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (originalImageUri != null) {
                            AsyncImage(
                                model = originalImageUri,
                                contentDescription = "Original image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "原图片",
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                // 文件信息
                TechCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "文件名: ${result.originalImageId ?: "未知"}",
                            color = OnSurface
                        )
                        Text(
                            text = "文件类型: 图片",
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "文件大小: 未知",
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // 检测结果
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 水印检测结果
                TechCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "水印检测结果:",
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (result.isTampered) "有篡改" else "未篡改",
                            color = if (result.isTampered) Error else Success
                        )
                        if (result.tamperRatioPercent != null) {
                            Text(
                                text = "篡改比例: ${String.format("%.2f", result.tamperRatioPercent)}%",
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                // 点击查看详细
                TechButton(
                    text = "点击查看详细",
                    onClick = {
                        // 查看水印检测详细结果
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // 模型检测结果
                TechCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "模型检测结果:",
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (result.isTampered) "有篡改" else "未篡改",
                            color = if (result.isTampered) Error else Success
                        )
                        if (result.confidence != null) {
                            Text(
                                text = "置信度: ${result.confidence}",
                                color = OnSurfaceVariant
                            )
                        }
                        if (result.tamperRatioPercent != null) {
                            Text(
                                text = "篡改比例: ${String.format("%.2f", result.tamperRatioPercent)}%",
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                // 点击查看详细
                TechButton(
                    text = "点击查看详细",
                    onClick = {
                        // 查看模型检测详细结果
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

