package com.bl2617.tamperrecovery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.data.model.ImageListResponse
import com.bl2617.tamperrecovery.data.repository.ImageRepository
import com.bl2617.tamperrecovery.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 图片ViewModel
 * 管理图片列表和详情的数据状态
 */
class ImageViewModel(
    context: Context? = null
) : ViewModel() {
    
    private val repository: ImageRepository = if (context != null) {
        val token = AuthManager.getToken(context)
        ImageRepository(token)
    } else {
        ImageRepository()
    }
    
    // 图片列表状态
    private val _imageListState = MutableStateFlow<ImageListState>(ImageListState.Loading)
    val imageListState: StateFlow<ImageListState> = _imageListState.asStateFlow()
    
    // 当前选中的图片
    private val _selectedImage = MutableStateFlow<ImageData?>(null)
    val selectedImage: StateFlow<ImageData?> = _selectedImage.asStateFlow()
    
    // 图片详情状态
    private val _imageDetailState = MutableStateFlow<ImageDetailState>(ImageDetailState.Idle)
    val imageDetailState: StateFlow<ImageDetailState> = _imageDetailState.asStateFlow()
    
    // 上传状态
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    // 当前页码
    private var currentPage = 1
    private val pageSize = 20
    private var currentCategory: String? = null
    
    init {
        // 只在有 token 时自动加载图片列表
        if (context != null && AuthManager.getToken(context) != null) {
            loadImageList()
        }
    }
    
    /**
     * 加载图片列表
     */
    fun loadImageList(page: Int = 1, category: String? = null, refresh: Boolean = false) {
        viewModelScope.launch {
            try {
                currentPage = page
                currentCategory = category
                
                if (refresh || page == 1) {
                    _imageListState.value = ImageListState.Loading
                } else {
                    // 加载更多时，保持当前状态，在底部显示加载指示器
                    val currentState = _imageListState.value
                    if (currentState is ImageListState.Success) {
                        // 保持当前图片列表，稍后合并
                    } else {
                        _imageListState.value = ImageListState.LoadingMore
                    }
                }
                
                repository.getImageList(page, pageSize, category).fold(
                    onSuccess = { response ->
                        val imageList = response.data?.images ?: emptyList()
                        val total = response.data?.total ?: 0
                        val hasMore = (page * pageSize) < total
                        
                        // 如果是加载更多，合并列表
                        val currentState = _imageListState.value
                        val finalList = if (currentState is ImageListState.Success && page > 1) {
                            currentState.images + imageList
                        } else {
                            imageList
                        }
                        
                        _imageListState.value = ImageListState.Success(
                            images = finalList,
                            hasMore = hasMore,
                            total = total
                        )
                    },
                    onFailure = { exception ->
                        _imageListState.value = ImageListState.Error(
                            message = exception.message ?: "加载失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _imageListState.value = ImageListState.Error(
                    message = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 刷新图片列表
     */
    fun refreshImageList() {
        loadImageList(page = 1, category = currentCategory, refresh = true)
    }
    
    /**
     * 加载更多图片
     */
    fun loadMoreImages() {
        val currentState = _imageListState.value
        if (currentState is ImageListState.Success && currentState.hasMore) {
            loadImageList(page = currentPage + 1, category = currentCategory, refresh = false)
        }
    }
    
    /**
     * 根据分类筛选
     */
    fun filterByCategory(category: String?) {
        loadImageList(page = 1, category = category, refresh = true)
    }
    
    /**
     * 加载图片详情
     */
    fun loadImageDetail(imageId: String) {
        viewModelScope.launch {
            _imageDetailState.value = ImageDetailState.Loading
            
            repository.getImageById(imageId).fold(
                onSuccess = { imageData ->
                    _selectedImage.value = imageData
                    _imageDetailState.value = ImageDetailState.Success(imageData)
                },
                onFailure = { exception ->
                    _imageDetailState.value = ImageDetailState.Error(
                        message = exception.message ?: "加载失败"
                    )
                }
            )
        }
    }
    
    /**
     * 清除选中的图片
     */
    fun clearSelectedImage() {
        _selectedImage.value = null
        _imageDetailState.value = ImageDetailState.Idle
    }
    
    /**
     * 清除所有状态（用于退出登录时）
     */
    fun clearAllState() {
        _imageListState.value = ImageListState.Loading
        _selectedImage.value = null
        _imageDetailState.value = ImageDetailState.Idle
        _uploadState.value = UploadState.Idle
        currentPage = 1
        currentCategory = null
    }
    
    /**
     * 上传图片
     */
    fun uploadImage(imageFile: java.io.File, category: String? = null, key: String? = null, encryptKey: String? = null) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                repository.uploadImage(imageFile, category, key, encryptKey).fold(
                    onSuccess = { imageData ->
                        _uploadState.value = UploadState.Success(imageData)
                        // 上传成功后刷新图片列表
                        refreshImageList()
                    },
                    onFailure = { exception ->
                        _uploadState.value = UploadState.Error(
                            message = exception.message ?: "上传失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(
                    message = e.message ?: "上传失败"
                )
            }
        }
    }
}

/**
 * 图片列表状态
 */
sealed class ImageListState {
    object Loading : ImageListState()
    object LoadingMore : ImageListState()
    data class Success(
        val images: List<ImageData>,
        val hasMore: Boolean,
        val total: Int
    ) : ImageListState()
    data class Error(val message: String) : ImageListState()
}

/**
 * 图片详情状态
 */
sealed class ImageDetailState {
    object Idle : ImageDetailState()
    object Loading : ImageDetailState()
    data class Success(val image: ImageData) : ImageDetailState()
    data class Error(val message: String) : ImageDetailState()
}

/**
 * 上传状态
 */
sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val image: ImageData) : UploadState()
    data class Error(val message: String) : UploadState()
}

