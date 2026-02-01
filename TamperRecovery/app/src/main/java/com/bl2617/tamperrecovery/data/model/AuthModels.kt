package com.bl2617.tamperrecovery.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户注册请求
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("device_id")
    val deviceId: String? = null
)

/**
 * 用户登录请求
 */
data class LoginRequest(
    val username: String,
    val password: String,
    @SerializedName("device_id")
    val deviceId: String? = null
)

/**
 * 用户信息
 */
data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_admin")
    val isAdmin: Boolean,
    @SerializedName("created_at")
    val createdAt: String
)

/**
 * Token 响应
 */
data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String = "bearer",
    val user: UserInfo? = null,
    val code: Int? = null,
    val message: String? = null
)
