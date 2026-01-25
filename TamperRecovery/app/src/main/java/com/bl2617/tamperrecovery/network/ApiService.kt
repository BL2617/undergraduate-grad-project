package com.bl2617.tamperrecovery.network

import com.bl2617.tamperrecovery.data.model.BaseResponse
import com.bl2617.tamperrecovery.data.model.ImageListResponse
import com.bl2617.tamperrecovery.data.model.ImageResponse
import com.bl2617.tamperrecovery.data.model.IncrementalTransferResponse
import com.bl2617.tamperrecovery.data.model.TamperDetectionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
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
     * @param token 认证Token（可选）
     * @return 图片响应数据
     */
    @GET("api/images/{imageId}")
    suspend fun getImageById(
        @Path("imageId") imageId: String,
        @Header("Authorization") token: String? = null
    ): Response<ImageResponse>
    
    /**
     * 获取图片列表
     * @param page 页码，从1开始
     * @param pageSize 每页数量
     * @param category 图片分类（可选）
     * @param token 认证Token（可选）
     * @return 图片列表响应数据
     */
    @GET("api/images")
    suspend fun getImageList(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("category") category: String? = null,
        @Header("Authorization") token: String? = null
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
        @Path("imageId") imageId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>
    
    /**
     * 上传图片
     * @param token 认证Token
     * @param file 图片文件
     * @param category 图片分类（可选）
     * @param key 水印密钥（可选）
     * @param encryptKey 加密密钥（可选）
     */
    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("category") category: RequestBody? = null,
        @Part("key") key: RequestBody? = null,
        @Part("encrypt_key") encryptKey: RequestBody? = null
    ): Response<ImageResponse>
    
    /**
     * 检测图片篡改
     * @param token 认证Token
     * @param file 待检测的图片文件
     * @param key 水印密钥
     */
    @Multipart
    @POST("api/detect")
    suspend fun detectTamper(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("key") key: RequestBody
    ): Response<TamperDetectionResponse>
    
    /**
     * 增量传输（获取篡改区域数据）
     * @param token 认证Token
     * @param imageId 图片ID
     * @param key 水印密钥
     */
    @POST("api/images/{imageId}/incremental-transfer")
    @FormUrlEncoded
    suspend fun incrementalTransfer(
        @Header("Authorization") token: String,
        @Path("imageId") imageId: String,
        @Field("key") key: String
    ): Response<IncrementalTransferResponse>
    
    /**
     * 局部恢复
     * @param token 认证Token
     * @param imageId 图片ID
     * @param region 需要恢复的区域，格式: "x1,y1,x2,y2"
     * @param decryptKey 解密密钥
     * @param backupFile 加密的原始备份文件
     */
    @Multipart
    @POST("api/images/{imageId}/recover-region")
    suspend fun recoverRegion(
        @Header("Authorization") token: String,
        @Path("imageId") imageId: String,
        @Part("region") region: RequestBody,
        @Part("decrypt_key") decryptKey: RequestBody,
        @Part backupFile: MultipartBody.Part
    ): Response<BaseResponse>
}


