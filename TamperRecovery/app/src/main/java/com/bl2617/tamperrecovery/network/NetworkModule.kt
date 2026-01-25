package com.bl2617.tamperrecovery.network

import android.content.Context
import android.content.pm.ApplicationInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
    // 真机测试（热点连接）: http://192.168.137.1:8000/ (Windows热点默认IP)
    // 真机测试（同一WiFi）: http://your-computer-ip:8000/ (替换为你的电脑IP地址)
    // 获取IP方法：Windows执行 ipconfig，查找"IPv4 地址"
    private const val BASE_URL = "http://192.168.0.122:8000/"

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
     * 创建OkHttpClient实例
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (isDebugMode) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * 创建Retrofit实例
     */
    private fun createRetrofit(baseUrl: String = BASE_URL): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }
    
    /**
     * API服务实例（单例）
     */
    val apiService: ApiService by lazy {
        createRetrofit().create(ApiService::class.java)
    }
    
    /**
     * 认证API服务实例（单例）
     */
    val authApiService: com.bl2617.tamperrecovery.network.AuthApiService by lazy {
        createRetrofit().create(com.bl2617.tamperrecovery.network.AuthApiService::class.java)
    }
    
    /**
     * 创建自定义基础URL的API服务
     * @param baseUrl 自定义的基础URL
     */
    fun createApiService(baseUrl: String): ApiService {
        return createRetrofit(baseUrl).create(ApiService::class.java)
    }
    
    /**
     * 创建带认证拦截器的Retrofit（用于需要Token的请求）
     */
    private fun createAuthenticatedRetrofit(token: String, baseUrl: String = BASE_URL): Retrofit {
        val authInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }
        
        val client = createOkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }
    
    /**
     * 创建带认证的API服务
     */
    fun createAuthenticatedApiService(token: String): ApiService {
        return createAuthenticatedRetrofit(token).create(ApiService::class.java)
    }
    
    /**
     * 创建带认证的Auth API服务
     */
    fun createAuthenticatedAuthApiService(token: String): com.bl2617.tamperrecovery.network.AuthApiService {
        return createAuthenticatedRetrofit(token).create(com.bl2617.tamperrecovery.network.AuthApiService::class.java)
    }
}

