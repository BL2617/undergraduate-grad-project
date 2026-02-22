package com.bl2617.tamperrecovery.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bl2617.tamperrecovery.data.model.*
import com.bl2617.tamperrecovery.network.ApiService
import com.bl2617.tamperrecovery.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

/**
 * 检测数据仓库
 * 封装检测相关的网络请求逻辑
 */
class DetectionRepository(
    private val apiService: ApiService = NetworkModule.apiService
) {
    
    /**
     * 创建带认证的 DetectionRepository
     * @param token JWT token
     */
    constructor(token: String?) : this(
        if (token != null) {
            NetworkModule.createAuthenticatedApiService(token)
        } else {
            NetworkModule.apiService
        }
    )
    
    /**
     * LSB水印检测（方式1）
     */
    suspend fun detectLSB(
        imageFile: File,
        key: String
    ): Result<DetectionResultData> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
                val keyPart = key.toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = apiService.detectLSB(filePart, keyPart)
                
                if (response.isSuccessful && response.body() != null) {
                    val detectionResponse = response.body()!!
                    if (detectionResponse.code == 200) {
                        Result.success(detectionResponse.data)
                    } else {
                        Result.failure(
                            Exception("检测失败: ${detectionResponse.message}")
                        )
                    }
                } else {
                    Result.failure(
                        Exception("网络请求失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 分块比对检测（方式2）
     */
    suspend fun detectCompare(
        originalImageId: String,
        imageFile: File,
        blockSize: Int = 64,
        threshold: Float = 0.1f
    ): Result<BlockComparisonResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
                val originalIdPart = originalImageId.toRequestBody("text/plain".toMediaTypeOrNull())
                val blockSizePart = blockSize.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val thresholdPart = threshold.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = apiService.detectCompare(originalIdPart, filePart, blockSizePart, thresholdPart)
                
                if (response.isSuccessful && response.body() != null) {
                    val comparisonResponse = response.body()!!
                    if (comparisonResponse.code == 200) {
                        Result.success(comparisonResponse)
                    } else {
                        Result.failure(
                            Exception("检测失败: ${comparisonResponse.message}")
                        )
                    }
                } else {
                    Result.failure(
                        Exception("网络请求失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 模型检测（方式3）
     */
    suspend fun detectModel(
        imageFile: File,
        confidenceThreshold: Float = 0.5f
    ): Result<DetectionResultData> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
                val thresholdPart = confidenceThreshold.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = apiService.detectModel(filePart, thresholdPart)
                
                if (response.isSuccessful && response.body() != null) {
                    val detectionResponse = response.body()!!
                    if (detectionResponse.code == 200) {
                        Result.success(detectionResponse.data)
                    } else {
                        Result.failure(
                            Exception("检测失败: ${detectionResponse.message}")
                        )
                    }
                } else {
                    Result.failure(
                        Exception("网络请求失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 获取可视化图片
     */
    suspend fun getVisualization(detectionResultId: String): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getVisualization(detectionResultId)
                if (response.isSuccessful && response.body() != null) {
                    val body: ResponseBody = response.body()!!
                    val inputStream = body.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    
                    if (bitmap != null) {
                        Result.success(bitmap)
                    } else {
                        Result.failure(IOException("无法解析图片数据"))
                    }
                } else {
                    Result.failure(
                        Exception("获取可视化图片失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 获取被篡改的块信息
     */
    suspend fun getTamperedBlocks(detectionResultId: String): Result<List<TamperedBlockInfo>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTamperedBlocks(detectionResultId)
                if (response.isSuccessful && response.body() != null) {
                    val blocksResponse = response.body()!!
                    if (blocksResponse.code == 200) {
                        Result.success(blocksResponse.data)
                    } else {
                        Result.failure(
                            Exception("获取块信息失败: ${blocksResponse.message}")
                        )
                    }
                } else {
                    Result.failure(
                        Exception("网络请求失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 恢复被篡改的块
     */
    suspend fun restoreBlocks(
        detectionResultId: String,
        blockIndices: List<Int>
    ): Result<List<RestoreBlockData>> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RestoreBlocksRequest(detectionResultId, blockIndices)
                val response = apiService.restoreBlocks(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val restoreResponse = response.body()!!
                    if (restoreResponse.code == 200) {
                        Result.success(restoreResponse.data)
                    } else {
                        Result.failure(
                            Exception("恢复失败: ${restoreResponse.message}")
                        )
                    }
                } else {
                    Result.failure(
                        Exception("网络请求失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}


