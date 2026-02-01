package com.bl2617.tamperrecovery.network

import com.bl2617.tamperrecovery.data.model.LoginRequest
import com.bl2617.tamperrecovery.data.model.RegisterRequest
import com.bl2617.tamperrecovery.data.model.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 认证 API 服务
 * 处理用户登录、注册等认证相关请求
 */
interface AuthApiService {
    
    /**
     * 用户注册
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): TokenResponse
    
    /**
     * 用户登录
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse
}
