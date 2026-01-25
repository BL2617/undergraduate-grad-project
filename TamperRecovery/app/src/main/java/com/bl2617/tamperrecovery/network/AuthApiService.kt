package com.bl2617.tamperrecovery.network

import com.bl2617.tamperrecovery.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 认证API接口
 */
interface AuthApiService {
    
    /**
     * 用户注册
     */
    @POST("api/auth/register")
    suspend fun register(
        @Body request: UserRegisterRequest
    ): Response<TokenResponse>
    
    /**
     * 用户登录
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: UserLoginRequest
    ): Response<TokenResponse>
    
    /**
     * 退出登录
     */
    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<BaseResponse>
    
    /**
     * 获取当前用户信息
     */
    @GET("api/auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserInfo>
    
    /**
     * 获取操作日志
     */
    @GET("api/auth/logs")
    suspend fun getOperationLogs(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<OperationLogListResponse>
}





