package com.bl2617.tamperrecovery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl2617.tamperrecovery.data.model.*
import com.bl2617.tamperrecovery.data.repository.DetectionRepository
import com.bl2617.tamperrecovery.data.repository.ImageRepository
import com.bl2617.tamperrecovery.utils.AuthManager
import com.bl2617.tamperrecovery.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * 检测ViewModel
 * 管理检测相关的数据状态
 */
class DetectionViewModel(
    private val context: Context? = null
) : ViewModel() {
    
    private val detectionRepository: DetectionRepository = if (context != null) {
        val token = AuthManager.getToken(context)
        DetectionRepository(token)
    } else {
        DetectionRepository()
    }
    
    private val imageRepository: ImageRepository = if (context != null) {
        val token = AuthManager.getToken(context)
        ImageRepository(token)
    } else {
        ImageRepository()
    }
    
    // LSB检测状态
    private val _lsbDetectionState = MutableStateFlow<DetectionState>(DetectionState.Idle)
    val lsbDetectionState: StateFlow<DetectionState> = _lsbDetectionState.asStateFlow()
    
    // 分块比对检测状态
    private val _compareDetectionState = MutableStateFlow<DetectionState>(DetectionState.Idle)
    val compareDetectionState: StateFlow<DetectionState> = _compareDetectionState.asStateFlow()
    
    // 模型检测状态
    private val _modelDetectionState = MutableStateFlow<DetectionState>(DetectionState.Idle)
    val modelDetectionState: StateFlow<DetectionState> = _modelDetectionState.asStateFlow()
    
    // 可视化图片状态
    private val _visualizationState = MutableStateFlow<VisualizationState>(VisualizationState.Idle)
    val visualizationState: StateFlow<VisualizationState> = _visualizationState.asStateFlow()
    
    // 图片列表（用于分块比对时选择原图）
    private val _imageListForSelection = MutableStateFlow<List<ImageData>>(emptyList())
    val imageListForSelection: StateFlow<List<ImageData>> = _imageListForSelection.asStateFlow()
    
    // 检测历史状态
    private val _detectionHistoryState = MutableStateFlow<DetectionHistoryState>(DetectionHistoryState.Idle)
    val detectionHistoryState: StateFlow<DetectionHistoryState> = _detectionHistoryState.asStateFlow()
    
    /**
     * LSB检测
     */
    fun detectLSB(imageFile: File, key: String) {
        viewModelScope.launch {
            _lsbDetectionState.value = DetectionState.Loading
            detectionRepository.detectLSB(imageFile, key).fold(
                onSuccess = { result ->
                    _lsbDetectionState.value = DetectionState.Success(result)
                },
                onFailure = { exception ->
                    _lsbDetectionState.value = DetectionState.Error(exception.message ?: "检测失败")
                }
            )
        }
    }
    
    /**
     * 分块比对检测
     */
    fun detectCompare(
        originalImageId: String,
        imageFile: File,
        blockSize: Int = 64
    ) {
        viewModelScope.launch {
            _compareDetectionState.value = DetectionState.Loading
            detectionRepository.detectCompare(originalImageId, imageFile, blockSize).fold(
                onSuccess = { response ->
                    _compareDetectionState.value = DetectionState.Success(response.data)
                },
                onFailure = { exception ->
                    _compareDetectionState.value = DetectionState.Error(exception.message ?: "检测失败")
                }
            )
        }
    }
    
    /**
     * 模型检测
     */
    fun detectModel(imageFile: File, confidenceThreshold: Float = Constants.MODEL_DETECTION_THRESHOLD) {
        viewModelScope.launch {
            _modelDetectionState.value = DetectionState.Loading
            detectionRepository.detectModel(imageFile, confidenceThreshold).fold(
                onSuccess = { result ->
                    _modelDetectionState.value = DetectionState.Success(result)
                },
                onFailure = { exception ->
                    _modelDetectionState.value = DetectionState.Error(exception.message ?: "检测失败")
                }
            )
        }
    }
    
    /**
     * LSB水印检测（从URI）
     */
    fun detectLSBWatermark(uri: android.net.Uri, key: String = "", callback: (DetectionResultData) -> Unit) {
        viewModelScope.launch {
            if (context != null) {
                val imageFile = createTempFileFromUri(context, uri, "temp_image_", ".jpg")
                if (imageFile != null) {
                    detectionRepository.detectLSB(imageFile, key).fold(
                        onSuccess = { result ->
                            callback(result)
                        },
                        onFailure = { exception ->
                            // 创建一个错误的结果对象
                            val errorResult = DetectionResultData(
                                id = "error",
                                detectionType = "lsb",
                                originalImageId = null,
                                detectedImageId = null,
                                isTampered = false,
                                tamperRatio = null,
                                tamperRatioPercent = 0.0f,
                                confidence = null,
                                tamperedRegions = null,
                                visualizationUrl = null,
                                createdAt = null
                            )
                            callback(errorResult)
                        }
                    )
                    // 清理临时文件
                    imageFile.delete()
                } else {
                    // 创建一个错误的结果对象
                    val errorResult = DetectionResultData(
                        id = "error",
                        detectionType = "lsb",
                        originalImageId = null,
                        detectedImageId = null,
                        isTampered = false,
                        tamperRatio = null,
                        tamperRatioPercent = 0.0f,
                        confidence = null,
                        tamperedRegions = null,
                        visualizationUrl = null,
                        createdAt = null
                    )
                    callback(errorResult)
                }
            } else {
                // 创建一个错误的结果对象
                val errorResult = DetectionResultData(
                    id = "error",
                    detectionType = "lsb",
                    originalImageId = null,
                    detectedImageId = null,
                    isTampered = false,
                    tamperRatio = null,
                    tamperRatioPercent = 0.0f,
                    confidence = null,
                    tamperedRegions = null,
                    visualizationUrl = null,
                    createdAt = null
                )
                callback(errorResult)
            }
        }
    }
    
    /**
     * 模型检测（从URI）
     */
    fun detectWithModel(uri: android.net.Uri, confidenceThreshold: Float = Constants.MODEL_DETECTION_THRESHOLD, callback: (DetectionResultData) -> Unit) {
        viewModelScope.launch {
            if (context != null) {
                val imageFile = createTempFileFromUri(context, uri, "temp_image_", ".jpg")
                if (imageFile != null) {
                    detectionRepository.detectModel(imageFile, confidenceThreshold).fold(
                        onSuccess = { result ->
                            callback(result)
                        },
                        onFailure = { exception ->
                            // 创建一个错误的结果对象
                            val errorResult = DetectionResultData(
                                id = "error",
                                detectionType = "model",
                                originalImageId = null,
                                detectedImageId = null,
                                isTampered = false,
                                tamperRatio = null,
                                tamperRatioPercent = 0.0f,
                                confidence = null,
                                tamperedRegions = null,
                                visualizationUrl = null,
                                createdAt = null
                            )
                            callback(errorResult)
                        }
                    )
                    // 清理临时文件
                    imageFile.delete()
                } else {
                    // 创建一个错误的结果对象
                    val errorResult = DetectionResultData(
                        id = "error",
                        detectionType = "model",
                        originalImageId = null,
                        detectedImageId = null,
                        isTampered = false,
                        tamperRatio = null,
                        tamperRatioPercent = 0.0f,
                        confidence = null,
                        tamperedRegions = null,
                        visualizationUrl = null,
                        createdAt = null
                    )
                    callback(errorResult)
                }
            } else {
                // 创建一个错误的结果对象
                val errorResult = DetectionResultData(
                    id = "error",
                    detectionType = "model",
                    originalImageId = null,
                    detectedImageId = null,
                    isTampered = false,
                    tamperRatio = null,
                    tamperRatioPercent = 0.0f,
                    confidence = null,
                    tamperedRegions = null,
                    visualizationUrl = null,
                    createdAt = null
                )
                callback(errorResult)
            }
        }
    }
    
    /**
     * 从URI创建临时文件
     */
    private fun createTempFileFromUri(context: android.content.Context, uri: android.net.Uri, prefix: String, suffix: String): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val tempFile = File.createTempFile(prefix, suffix)
                val outputStream = tempFile.outputStream()
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                return tempFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    
    /**
     * 获取可视化图片
     */
    fun loadVisualization(detectionResultId: String) {
        viewModelScope.launch {
            _visualizationState.value = VisualizationState.Loading
            detectionRepository.getVisualization(detectionResultId).fold(
                onSuccess = { bitmap ->
                    _visualizationState.value = VisualizationState.Success(bitmap)
                },
                onFailure = { exception ->
                    _visualizationState.value = VisualizationState.Error(exception.message ?: "加载失败")
                }
            )
        }
    }
    
    /**
     * 加载图片列表（用于分块比对时选择原图）
     */
    fun loadImageListForSelection() {
        viewModelScope.launch {
            imageRepository.getImageList(1, 100, null).fold(
                onSuccess = { response ->
                    _imageListForSelection.value = response.data?.images ?: emptyList()
                },
                onFailure = {
                    // 静默失败，不影响UI
                }
            )
        }
    }
    


    fun clearLsbState() {
        _lsbDetectionState.value = DetectionState.Idle
    }

    fun clearCompareState() {
        _compareDetectionState.value = DetectionState.Idle
    }

    fun clearModelState() {
        _modelDetectionState.value = DetectionState.Idle
    }

    fun clearVisualizationState() {
        _visualizationState.value = VisualizationState.Idle
    }

    fun clearImageListForSelection() {
        _imageListForSelection.value = emptyList()
    }
    
    /**
     * 加载检测历史
     */
    fun loadDetectionHistory(page: Int = 1, pageSize: Int = 10, detectionType: String? = null) {
        viewModelScope.launch {
            _detectionHistoryState.value = DetectionHistoryState.Loading
            detectionRepository.getDetectionHistory(page, pageSize, detectionType).fold(
                onSuccess = { results ->
                    _detectionHistoryState.value = DetectionHistoryState.Success(results)
                },
                onFailure = { exception ->
                    _detectionHistoryState.value = DetectionHistoryState.Error(exception.message ?: "加载失败")
                }
            )
        }
    }
    
    /**
     * 清除检测历史状态
     */
    fun clearDetectionHistoryState() {
        _detectionHistoryState.value = DetectionHistoryState.Idle
    }
    
    /**
     * 清除所有状态
     */
    fun clearAllState() {
        _lsbDetectionState.value = DetectionState.Idle
        _compareDetectionState.value = DetectionState.Idle
        _modelDetectionState.value = DetectionState.Idle
        _visualizationState.value = VisualizationState.Idle
        _imageListForSelection.value = emptyList()
        _detectionHistoryState.value = DetectionHistoryState.Idle
    }
}

/**
 * 检测状态
 */
sealed class DetectionState {
    object Idle : DetectionState()
    object Loading : DetectionState()
    data class Success(val result: DetectionResultData) : DetectionState()
    data class Error(val message: String) : DetectionState()
}

/**
 * 可视化状态
 */
sealed class VisualizationState {
    object Idle : VisualizationState()
    object Loading : VisualizationState()
    data class Success(val bitmap: android.graphics.Bitmap) : VisualizationState()
    data class Error(val message: String) : VisualizationState()
}

/**
 * 检测历史状态
 */
sealed class DetectionHistoryState {
    object Idle : DetectionHistoryState()
    object Loading : DetectionHistoryState()
    data class Success(val results: List<DetectionResultData>) : DetectionHistoryState()
    data class Error(val message: String) : DetectionHistoryState()
}

