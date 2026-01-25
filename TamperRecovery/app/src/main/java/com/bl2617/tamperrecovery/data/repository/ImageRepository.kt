package com.bl2617.tamperrecovery.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.data.model.ImageListResponse
import com.bl2617.tamperrecovery.data.model.ImageResponse
import com.bl2617.tamperrecovery.network.ApiService
import com.bl2617.tamperrecovery.network.NetworkModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.IOException

/**
 * 图片数据仓库
 * 封装图片相关的网络请求逻辑
 */
class ImageRepository(
    private val apiService: ApiService = NetworkModule.apiService,
    private val token: String? = null
) {
    /** 构造带 Bearer 前缀的 Authorization 头 */
    private fun bearer(): String? = token?.let { "Bearer $it" }
    
    /**
     * 根据ID获取图片信息
     * @param imageId 图片ID
     * @return Result包装的ImageData，成功时包含图片数据，失败时包含异常信息
     */
    suspend fun getImageById(imageId: String): Result<ImageData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = if (token != null) {
                    apiService.getImageById(imageId, bearer())
                } else {
                    apiService.getImageById(imageId)
                }
                if (response.isSuccessful && response.body() != null) {
                    val imageResponse = response.body()!!
                    if (imageResponse.code == 200 && imageResponse.data != null) {
                        Result.success(imageResponse.data)
                    } else {
                        Result.failure(
                            Exception("获取图片失败: ${imageResponse.message}")
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
     * 获取图片列表
     * @param page 页码，从1开始
     * @param pageSize 每页数量
     * @param category 图片分类（可选）
     * @return Result包装的ImageListResponse，成功时包含图片列表，失败时包含异常信息
     */
    suspend fun getImageList(
        page: Int = 1,
        pageSize: Int = 20,
        category: String? = null
    ): Result<ImageListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = if (token != null) {
                    apiService.getImageList(page, pageSize, category, bearer())
                } else {
                    apiService.getImageList(page, pageSize, category)
                }
                if (response.isSuccessful && response.body() != null) {
                    val listResponse = response.body()!!
                    if (listResponse.code == 200) {
                        Result.success(listResponse)
                    } else {
                        Result.failure(
                            Exception("获取图片列表失败: ${listResponse.message}")
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
     * 下载图片为Bitmap
     * @param imageUrl 图片的完整URL
     * @return Result包装的Bitmap，成功时包含图片Bitmap，失败时包含异常信息
     */
    suspend fun downloadImageAsBitmap(imageUrl: String): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.downloadImage(imageUrl)
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
                        Exception("下载图片失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 上传图片
     */
    suspend fun uploadImage(
        fileName: String,
        bytes: ByteArray,
        category: String? = null,
        key: String? = null,
        encryptKey: String? = null
    ): Result<ImageResponse> {
        if (token == null) return Result.failure(Exception("需要认证 Token 才能上传图片"))

        return withContext(Dispatchers.IO) {
            try {
                val imageRequestBody: RequestBody =
                    bytes.toRequestBody("image/*".toMediaType())
                val filePart = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = fileName,
                    body = imageRequestBody
                )

                val categoryBody = category?.toRequestBody("text/plain".toMediaType())
                val keyBody = key?.toRequestBody("text/plain".toMediaType())
                val encryptKeyBody = encryptKey?.toRequestBody("text/plain".toMediaType())

                val response = apiService.uploadImage(
                    token = bearer()!!,
                    file = filePart,
                    category = categoryBody,
                    key = keyBody,
                    encryptKey = encryptKeyBody
                )

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(
                        Exception("上传失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 通过图片ID下载图片为Bitmap
     * @param imageId 图片ID
     * @return Result包装的Bitmap，成功时包含图片Bitmap，失败时包含异常信息
     */
    suspend fun downloadImageByIdAsBitmap(imageId: String): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            try {
                if (token == null) {
                    return@withContext Result.failure(
                        Exception("需要认证Token才能下载图片")
                    )
                }
                val response = apiService.downloadImageById(imageId, bearer()!!)
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
                        Exception("下载图片失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 下载图片为字节数组
     * @param imageUrl 图片的完整URL
     * @return Result包装的ByteArray，成功时包含图片字节数组，失败时包含异常信息
     */
    suspend fun downloadImageAsBytes(imageUrl: String): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.downloadImage(imageUrl)
                if (response.isSuccessful && response.body() != null) {
                    val body: ResponseBody = response.body()!!
                    val bytes = body.bytes()
                    Result.success(bytes)
                } else {
                    Result.failure(
                        Exception("下载图片失败: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}


