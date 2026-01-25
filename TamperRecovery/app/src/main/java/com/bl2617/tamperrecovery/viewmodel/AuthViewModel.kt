package com.bl2617.tamperrecovery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl2617.tamperrecovery.data.model.UserLoginRequest
import com.bl2617.tamperrecovery.data.model.UserRegisterRequest
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 认证ViewModel
 * 管理用户登录、注册和认证状态
 */
class AuthViewModel(private val context: Context) : ViewModel() {
    
    private val authApiService = NetworkModule.authApiService
    
    // 认证状态
    private val _authState = MutableStateFlow<AuthState>(
        if (AuthManager.isLoggedIn(context)) AuthState.Authenticated else AuthState.Unauthenticated
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // 登录状态
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    /**
     * 用户登录
     */
    fun login(username: String, password: String, deviceId: String? = null) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                
                val request = UserLoginRequest(username, password, deviceId)
                val response = authApiService.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val tokenResponse = response.body()!!
                    if (tokenResponse.code == 200 && tokenResponse.accessToken != null) {
                        // 保存 token
                        AuthManager.saveToken(context, tokenResponse.accessToken)
                        
                        // 保存用户信息
                        tokenResponse.user?.let { user ->
                            AuthManager.saveUserInfo(context, user.username, user.id)
                        }
                        
                        _authState.value = AuthState.Authenticated
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error(tokenResponse.message)
                    }
                } else {
                    _loginState.value = LoginState.Error("登录失败: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "登录失败")
            }
        }
    }
    
    /**
     * 用户注册
     */
    fun register(username: String, email: String, password: String, deviceId: String? = null) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                
                val request = UserRegisterRequest(username, email, password, deviceId)
                val response = authApiService.register(request)
                
                android.util.Log.d("AuthViewModel", "Register response code: ${response.code()}")
                android.util.Log.d("AuthViewModel", "Register response body: ${response.body()}")
                
                if (response.isSuccessful && response.body() != null) {
                    val tokenResponse = response.body()!!
                    android.util.Log.d("AuthViewModel", "TokenResponse code: ${tokenResponse.code}")
                    android.util.Log.d("AuthViewModel", "TokenResponse accessToken: ${tokenResponse.accessToken}")
                    android.util.Log.d("AuthViewModel", "TokenResponse user: ${tokenResponse.user}")
                    
                    if (tokenResponse.code == 200 && tokenResponse.accessToken != null) {
                        // 保存 token
                        AuthManager.saveToken(context, tokenResponse.accessToken)
                        android.util.Log.d("AuthViewModel", "Token saved: ${tokenResponse.accessToken}")
                        
                        // 保存用户信息
                        tokenResponse.user?.let { user ->
                            AuthManager.saveUserInfo(context, user.username, user.id)
                            android.util.Log.d("AuthViewModel", "User info saved: ${user.username}")
                        }
                        
                        _authState.value = AuthState.Authenticated
                        _loginState.value = LoginState.Success
                        android.util.Log.d("AuthViewModel", "Auth state set to Authenticated")
                    } else {
                        android.util.Log.e("AuthViewModel", "Token response invalid: code=${tokenResponse.code}, token=${tokenResponse.accessToken}")
                        _loginState.value = LoginState.Error(tokenResponse.message)
                    }
                } else {
                    android.util.Log.e("AuthViewModel", "Register failed: ${response.code()} ${response.message()}")
                    _loginState.value = LoginState.Error("注册失败: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Register exception", e)
                _loginState.value = LoginState.Error(e.message ?: "注册失败")
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        viewModelScope.launch {
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    // 调用后端退出登录接口（可选）
                    try {
                        authApiService.logout("Bearer $token")
                    } catch (e: Exception) {
                        // 忽略错误，本地清除即可
                    }
                }
                
                // 清除本地存储
                AuthManager.clearUserInfo(context)
                _authState.value = AuthState.Unauthenticated
                _loginState.value = LoginState.Idle
            } catch (e: Exception) {
                // 即使出错也清除本地状态
                AuthManager.clearUserInfo(context)
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    /**
     * 重置登录状态
     */
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

/**
 * 认证状态
 */
sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

/**
 * 登录状态
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

