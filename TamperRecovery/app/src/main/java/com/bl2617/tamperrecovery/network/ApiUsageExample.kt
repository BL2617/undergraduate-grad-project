package com.bl2617.tamperrecovery.network

/**
 * API使用示例
 * 展示如何在ViewModel或Composable中使用图片API接口
 * 
 * 注意：
 * 1. 如果使用ViewModel示例，需要添加以下依赖：
 *    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
 *    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
 * 
 * 2. 如果使用Compose示例，需要添加以下导入：
 *    import androidx.compose.foundation.layout.*
 *    import androidx.compose.foundation.lazy.LazyColumn
 *    import androidx.compose.foundation.lazy.items
 *    import androidx.compose.material3.*
 *    import androidx.compose.runtime.*
 *    import androidx.compose.ui.Modifier
 *    import coil.compose.AsyncImage
 *    import android.util.Log
 */

/*
// ========== 示例中需要的导入 ==========
import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.data.repository.ImageRepository
import kotlinx.coroutines.launch
*/

// ========== 示例1: 在ViewModel中使用（需要添加ViewModel依赖） ==========
/*
class ImageViewModel : ViewModel() {
    
    private val imageRepository = ImageRepository()
    
    var images by mutableStateOf<List<ImageData>>(emptyList())
        private set
    
    var currentImage by mutableStateOf<ImageData?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var downloadedBitmap by mutableStateOf<Bitmap?>(null)
        private set
    
    /**
     * 获取图片列表
     */
    fun loadImageList(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            imageRepository.getImageList(page, pageSize).fold(
                onSuccess = { response ->
                    images = response.data?.images ?: emptyList()
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "加载图片列表失败"
                }
            )
            
            isLoading = false
        }
    }
    
    /**
     * 根据ID获取单张图片信息
     */
    fun loadImageById(imageId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            imageRepository.getImageById(imageId).fold(
                onSuccess = { imageData ->
                    currentImage = imageData
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "加载图片信息失败"
                }
            )
            
            isLoading = false
        }
    }
    
    /**
     * 下载图片为Bitmap
     */
    fun downloadImage(imageUrl: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            imageRepository.downloadImageAsBitmap(imageUrl).fold(
                onSuccess = { bitmap ->
                    downloadedBitmap = bitmap
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "下载图片失败"
                }
            )
            
            isLoading = false
        }
    }
    
    /**
     * 通过ID下载图片
     */
    fun downloadImageById(imageId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            imageRepository.downloadImageByIdAsBitmap(imageId).fold(
                onSuccess = { bitmap ->
                    downloadedBitmap = bitmap
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "下载图片失败"
                }
            )
            
            isLoading = false
        }
    }
}
*/


// ========== 示例2: 在Composable中直接使用 ==========
/*
@Composable
fun ImageScreen() {
    var imageList by remember { mutableStateOf<List<ImageData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isLoading = true
        val repository = ImageRepository()
        repository.getImageList().fold(
            onSuccess = { response ->
                imageList = response.data?.images ?: emptyList()
            },
            onFailure = { exception ->
                // 处理错误
                Log.e("ImageScreen", "加载失败", exception)
            }
        )
        isLoading = false
    }
    
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            items(imageList) { image ->
                // 使用Coil加载图片
                AsyncImage(
                    model = image.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
*/

// ========== 示例3: 直接使用API Service ==========
/*
class MyRepository {
    private val apiService = NetworkModule.apiService
    
    suspend fun fetchImages(): List<ImageData>? {
        return try {
            val response = apiService.getImageList(page = 1, pageSize = 20)
            if (response.isSuccessful) {
                response.body()?.data?.images
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
*/

