package com.bl2617.tamperrecovery.data.model

import com.google.gson.annotations.SerializedName

/**
 * 检测响应数据模型
 */
data class DetectionResponse(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: DetectionResultData
)

/**
 * 检测结果数据
 */
data class DetectionResultData(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("detection_type")
    val detectionType: String, // lsb, compare, model
    
    @SerializedName("original_image_id")
    val originalImageId: String?,
    
    @SerializedName("detected_image_id")
    val detectedImageId: String?,
    
    @SerializedName("is_tampered")
    val isTampered: Boolean,
    
    @SerializedName("tamper_ratio")
    val tamperRatio: String?,
    
    @SerializedName("tamper_ratio_percent")
    val tamperRatioPercent: Float?,
    
    @SerializedName("confidence")
    val confidence: String?,
    
    @SerializedName("tampered_regions")
    val tamperedRegions: List<TamperedRegion>?,
    
    @SerializedName("visualization_url")
    val visualizationUrl: String?,
    
    @SerializedName("created_at")
    val createdAt: String?
)

/**
 * 篡改区域
 */
data class TamperedRegion(
    @SerializedName("x")
    val x: Int,
    
    @SerializedName("y")
    val y: Int,
    
    @SerializedName("width")
    val width: Int,
    
    @SerializedName("height")
    val height: Int,
    
    @SerializedName("confidence")
    val confidence: Float?
)

/**
 * 分块比对响应
 */
data class BlockComparisonResponse(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: DetectionResultData,
    
    @SerializedName("blocks")
    val blocks: List<BlockComparisonData>
)

/**
 * 分块比对数据
 */
data class BlockComparisonData(
    @SerializedName("block_index")
    val blockIndex: Int,
    
    @SerializedName("x")
    val x: Int,
    
    @SerializedName("y")
    val y: Int,
    
    @SerializedName("width")
    val width: Int,
    
    @SerializedName("height")
    val height: Int,
    
    @SerializedName("is_tampered")
    val isTampered: Boolean,
    
    @SerializedName("difference_ratio")
    val differenceRatio: Float?
)

/**
 * 被篡改的块响应
 */
data class TamperedBlocksResponse(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<TamperedBlockInfo>
)

/**
 * 被篡改的块信息
 */
data class TamperedBlockInfo(
    @SerializedName("block_index")
    val blockIndex: Int,
    
    @SerializedName("x")
    val x: Int,
    
    @SerializedName("y")
    val y: Int,
    
    @SerializedName("width")
    val width: Int,
    
    @SerializedName("height")
    val height: Int,
    
    @SerializedName("has_original_data")
    val hasOriginalData: Boolean
)

/**
 * 恢复块请求
 */
data class RestoreBlocksRequest(
    @SerializedName("detection_result_id")
    val detectionResultId: String,
    
    @SerializedName("block_indices")
    val blockIndices: List<Int>
)

/**
 * 恢复块响应
 */
data class RestoreBlocksResponse(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<RestoreBlockData>
)

/**
 * 恢复块数据
 */
data class RestoreBlockData(
    @SerializedName("block_index")
    val blockIndex: Int,
    
    @SerializedName("x")
    val x: Int,
    
    @SerializedName("y")
    val y: Int,
    
    @SerializedName("width")
    val width: Int,
    
    @SerializedName("height")
    val height: Int,
    
    @SerializedName("block_data")
    val blockData: String // base64编码的PNG数据
)
