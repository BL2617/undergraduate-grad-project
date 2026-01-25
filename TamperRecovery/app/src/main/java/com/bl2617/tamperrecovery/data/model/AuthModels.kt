package com.bl2617.tamperrecovery.data.model

/**
 * 认证相关数据模型
 */
data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val deviceId: String? = null
)

data class UserLoginRequest(
    val username: String,
    val password: String,
    val deviceId: String? = null
)

data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    val isActive: Boolean,
    val isAdmin: Boolean,
    val createdAt: String? = null
)

data class TokenResponse(
    val code: Int,
    val message: String,
    val accessToken: String? = null,
    val tokenType: String? = null,
    val user: UserInfo? = null
)






