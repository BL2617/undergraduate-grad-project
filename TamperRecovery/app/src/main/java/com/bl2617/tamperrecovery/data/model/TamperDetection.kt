package com.bl2617.tamperrecovery.data.model

/**
 * 篡改检测相关数据模型
 */
data class TamperRegion(
    val region: List<Int>,  // [x1, y1, x2, y2]
    val data: String? = null  // base64编码的区域图像数据
)

data class TamperDetectionData(
    val isTampered: Boolean,
    val tamperRatio: Double,
    val tamperRatioPercent: Double,
    val tamperRegions: List<List<Int>>? = null,  // 篡改区域列表
    val visualization: String? = null  // base64编码的可视化图像
)

data class TamperDetectionResponse(
    val code: Int,
    val message: String,
    val data: TamperDetectionData? = null
)

data class IncrementalTransferData(
    val hasTamper: Boolean,
    val tamperRatio: Double? = null,
    val regions: List<TamperRegion>? = null
)

data class IncrementalTransferResponse(
    val code: Int,
    val message: String,
    val data: IncrementalTransferData? = null
)

data class BaseResponse(
    val code: Int,
    val message: String,
    val data: Any? = null
)





