package com.bl2617.tamperrecovery.data.model

/**
 * 操作日志数据模型
 */
data class OperationLogData(
    val id: String,
    val operationType: String,
    val operationDesc: String? = null,
    val imageId: String? = null,
    val ipAddress: String? = null,
    val deviceInfo: String? = null,
    val createdAt: String
)

data class OperationLogListResponse(
    val code: Int,
    val message: String,
    val data: List<OperationLogData>? = null,
    val total: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null
)





