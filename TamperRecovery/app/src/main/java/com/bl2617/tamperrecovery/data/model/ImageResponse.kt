package com.bl2617.tamperrecovery.data.model

import com.google.gson.annotations.SerializedName

/**
 * 图片响应数据模型
 * 根据后端实际返回的JSON结构进行调整
 */
data class ImageResponse(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: ImageData?
)

/**
 * 图片数据
 */
data class ImageData(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("url")
    val url: String,
    
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String?,
    
    @SerializedName("width")
    val width: Int?,
    
    @SerializedName("height")
    val height: Int?,
    
    @SerializedName("size")
    val size: Long?,
    
    @SerializedName("format")
    val format: String?,
    
    @SerializedName("timestamp")
    val timestamp: Long?
)

/**
 * 图片列表响应
 */
data class ImageListResponse(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: ImageListData?
)

data class ImageListData(
    @SerializedName("images")
    val images: List<ImageData>,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("pageSize")
    val pageSize: Int
)



