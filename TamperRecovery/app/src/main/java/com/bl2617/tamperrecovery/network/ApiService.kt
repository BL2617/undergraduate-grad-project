package com.bl2617.tamperrecovery.network

import com.bl2617.tamperrecovery.data.model.ImageListResponse
import com.bl2617.tamperrecovery.data.model.ImageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import okhttp3.MultipartBody
import retrofit2.http.*

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
     * @param file 图片文件（MultipartBody.Part）
     * @param category 图片分类（可选）
     * @return 上传响应
     */
    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("category") category: String? = null
    ): Response<ImageResponse>
}



