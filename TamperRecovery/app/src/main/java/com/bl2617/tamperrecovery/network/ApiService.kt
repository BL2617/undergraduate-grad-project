package com.bl2617.tamperrecovery.network

import com.bl2617.tamperrecovery.data.model.ImageListResponse
import com.bl2617.tamperrecovery.data.model.ImageResponse
import okhttp3.ResponseBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * API接口定义
 * 定义所有与后端通信的接口
 */
interface ApiService {
    
    /**
     * 获取单张图片信息（通过ID）
     * @param imageId 图片ID
     * @return 图片响应数据
     */
    @GET("api/images/{imageId}")
    suspend fun getImageById(
        @Path("imageId") imageId: String
    ): Response<ImageResponse>
    
    /**
     * 获取图片列表
     * @param page 页码，从1开始
     * @param pageSize 每页数量
     * @param category 图片分类（可选）
     * @return 图片列表响应数据
     */
    @GET("api/images")
    suspend fun getImageList(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("category") category: String? = null
    ): Response<ImageListResponse>
    
    /**
     * 下载图片（通过URL）
     * 使用@Streaming注解以流的方式下载大文件
     * @param imageUrl 图片的完整URL
     * @return ResponseBody，包含图片的二进制数据
     */
    @Streaming
    @GET
    suspend fun downloadImage(
        @Url imageUrl: String
    ): Response<ResponseBody>
    
    /**
     * 下载图片（通过图片ID，使用默认图片接口）
     * @param imageId 图片ID
     * @return ResponseBody，包含图片的二进制数据
     */
    @Streaming
    @GET("api/images/{imageId}/download")
    suspend fun downloadImageById(
        @Path("imageId") imageId: String
    ): Response<ResponseBody>
    
    /**
     * 上传图片
     * @param file 图片文件
     * @param category 图片分类（可选）
     * @param key 水印密钥（可选）
     * @param encryptKey 加密密钥（可选）
     * @return 图片响应数据
     */
    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("category") category: okhttp3.RequestBody? = null,
        @Part("key") key: okhttp3.RequestBody? = null,
        @Part("encryptKey") encryptKey: okhttp3.RequestBody? = null
    ): Response<ImageResponse>
    
    /**
     * LSB水印检测（方式1）
     * @param file 待检测的图片文件
     * @param key 用户密钥
     * @return 检测结果
     */
    @Multipart
    @POST("api/detection/lsb")
    suspend fun detectLSB(
        @Part file: MultipartBody.Part,
        @Part("key") key: okhttp3.RequestBody
    ): Response<com.bl2617.tamperrecovery.data.model.DetectionResponse>
    
    /**
     * 分块比对检测（方式2）
     * @param originalImageId 原图ID
     * @param file 待检测的图片文件
     * @param blockSize 块大小（默认64）
     * @param threshold 差异阈值（默认0.1）
     * @return 检测结果
     */
    @Multipart
    @POST("api/detection/compare")
    suspend fun detectCompare(
        @Part("original_image_id") originalImageId: okhttp3.RequestBody,
        @Part file: MultipartBody.Part,
        @Part("block_size") blockSize: okhttp3.RequestBody? = null,
        @Part("threshold") threshold: okhttp3.RequestBody? = null
    ): Response<com.bl2617.tamperrecovery.data.model.BlockComparisonResponse>
    
    /**
     * 模型检测（方式3）
     * @param file 待检测的图片文件
     * @param confidenceThreshold 置信度阈值（默认0.5）
     * @return 检测结果
     */
    @Multipart
    @POST("api/detection/model")
    suspend fun detectModel(
        @Part file: MultipartBody.Part,
        @Part("confidence_threshold") confidenceThreshold: okhttp3.RequestBody? = null
    ): Response<com.bl2617.tamperrecovery.data.model.DetectionResponse>
    
    /**
     * 获取可视化图片
     * @param detectionResultId 检测结果ID
     * @return 可视化图片二进制数据
     */
    @Streaming
    @GET("api/detection/visualization/{detectionResultId}")
    suspend fun getVisualization(
        @Path("detectionResultId") detectionResultId: String
    ): Response<ResponseBody>
    
    /**
     * 获取被篡改的块信息
     * @param detectionResultId 检测结果ID
     * @return 块信息列表
     */
    @GET("api/recovery/blocks/{detectionResultId}")
    suspend fun getTamperedBlocks(
        @Path("detectionResultId") detectionResultId: String
    ): Response<com.bl2617.tamperrecovery.data.model.TamperedBlocksResponse>
    
    /**
     * 恢复被篡改的块
     * @param request 恢复请求
     * @return 恢复数据
     */
    @POST("api/recovery/restore-blocks")
    suspend fun restoreBlocks(
        @Body request: com.bl2617.tamperrecovery.data.model.RestoreBlocksRequest
    ): Response<com.bl2617.tamperrecovery.data.model.RestoreBlocksResponse>
}


