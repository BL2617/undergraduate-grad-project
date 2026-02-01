package com.bl2617.tamperrecovery.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl2617.tamperrecovery.data.model.LoginRequest
import com.bl2617.tamperrecovery.data.model.RegisterRequest
import com.bl2617.tamperrecovery.network.AuthApiService
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 认证状态
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * 认证 ViewModel
 * 管理用户登录、注册状态
 */
class AuthViewModel(private val context: Context) : ViewModel() {
    
    private val authApiService: AuthApiService = NetworkModule.createRetrofit()
        .create(AuthApiService::class.java)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return AuthManager.isLoggedIn(context)
    }
    
    /**
     * 用户注册
     */
    fun register(username: String, email: String, password: String, deviceId: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    deviceId = deviceId
                )
                val response = authApiService.register(request)
                
                // 保存 token 和用户信息
                AuthManager.saveToken(context, response.accessToken)
                response.user?.let {
                    AuthManager.saveUsername(context, it.username)
                }
                
                _authState.value = AuthState.Success("注册成功")
            } catch (e: Exception) {
                val errorMessage = e.message ?: "注册失败"
                Log.e("AuthViewModel", "Register error: ${e.message}", e)
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }
    
    /**
     * 用户登录
     */
    fun login(username: String, password: String, deviceId: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = LoginRequest(
                    username = username,
                    password = password,
                    deviceId = deviceId
                )
                val response = authApiService.login(request)
                
                // 保存 token 和用户信息
                AuthManager.saveToken(context, response.accessToken)
                response.user?.let {
                    AuthManager.saveUsername(context, it.username)
                }
                
                _authState.value = AuthState.Success("登录成功")
            } catch (e: Exception) {
                val errorMessage = e.message ?: "登录失败"
                Log.e("AuthViewModel", "Login error: ${e.message}", e)
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        AuthManager.clearAuth(context)
        _authState.value = AuthState.Idle
    }
}
