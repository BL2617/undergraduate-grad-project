package com.bl2617.tamperrecovery.network

import android.content.Context
import android.content.pm.ApplicationInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络模块配置
 * 提供Retrofit实例和相关配置
 */
object NetworkModule {
    
    // 基础URL - 根据实际后端地址修改
    // 本地开发: http://192.168.0.103:8000/ (Android模拟器访问本地主机)
    // 真机测试: http://your-computer-ip:8000/ (替换为你的电脑IP地址)
    private const val BASE_URL = "http://192.168.5.35:8000/"
    
    // 是否开启日志，可以通过外部设置
    var isDebugMode: Boolean = true
    
    /**
     * 初始化网络模块（可选）
     * @param context 用于检测是否为DEBUG模式
     */
    fun init(context: Context? = null) {
        if (context != null) {
            isDebugMode = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        }
    }
    
    /**
     * 创建Gson实例
     */
    private fun createGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
    }
    
    /**
     * 创建认证拦截器
     */
    private fun createAuthInterceptor(token: String?): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
            chain.proceed(newRequest)
        }
    }
    
    /**
     * 创建OkHttpClient实例
     */
    private fun createOkHttpClient(token: String? = null): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (isDebugMode) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        
        // 如果提供了 token，添加认证拦截器
        if (token != null) {
            builder.addInterceptor(createAuthInterceptor(token))
        }
        
        return builder.build()
    }
    
    /**
     * 创建Retrofit实例
     */
    fun createRetrofit(baseUrl: String = BASE_URL, token: String? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(token))
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }
    
    /**
     * API服务实例（单例，无认证）
     */
    val apiService: ApiService by lazy {
        createRetrofit().create(ApiService::class.java)
    }
    
    /**
     * 创建带认证的API服务实例
     * @param token JWT token
     */
    fun createAuthenticatedApiService(token: String?): ApiService {
        return createRetrofit(token = token).create(ApiService::class.java)
    }
    
    /**
     * 创建带认证的OkHttpClient（供Coil使用）
     * @param token JWT token
     */
    fun createAuthenticatedOkHttpClient(token: String?): OkHttpClient {
        return createOkHttpClient(token)
    }

}

