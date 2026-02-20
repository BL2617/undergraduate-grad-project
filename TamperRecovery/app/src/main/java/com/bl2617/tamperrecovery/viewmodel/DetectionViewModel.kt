package com.bl2617.tamperrecovery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl2617.tamperrecovery.data.model.*
import com.bl2617.tamperrecovery.data.repository.DetectionRepository
import com.bl2617.tamperrecovery.data.repository.ImageRepository
import com.bl2617.tamperrecovery.utils.AuthManager
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
    context: Context? = null
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
        blockSize: Int = 64,
        threshold: Float = 0.1f
    ) {
        viewModelScope.launch {
            _compareDetectionState.value = DetectionState.Loading
            detectionRepository.detectCompare(originalImageId, imageFile, blockSize, threshold).fold(
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
    fun detectModel(imageFile: File, confidenceThreshold: Float = 0.5f) {
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
    
    /**
     * 清除所有状态
     */
    fun clearAllState() {
        _lsbDetectionState.value = DetectionState.Idle
        _compareDetectionState.value = DetectionState.Idle
        _modelDetectionState.value = DetectionState.Idle
        _visualizationState.value = VisualizationState.Idle
        _imageListForSelection.value = emptyList()
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

