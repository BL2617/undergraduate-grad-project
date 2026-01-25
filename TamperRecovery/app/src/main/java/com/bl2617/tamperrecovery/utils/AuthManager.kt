package com.bl2617.tamperrecovery.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 认证管理器
 * 管理用户登录状态和Token
 */
object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_USERNAME = "username"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_DEVICE_ID = "device_id"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 保存Token
     */
    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }
    
    /**
     * 获取Token
     */
    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }
    
    /**
     * 清除Token（退出登录）
     */
    fun clearToken(context: Context) {
        getPrefs(context).edit().remove(KEY_TOKEN).apply()
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(context: Context): Boolean {
        return getToken(context) != null
    }
    
    /**
     * 保存用户信息
     */
    fun saveUserInfo(context: Context, username: String, userId: String) {
        getPrefs(context).edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_USER_ID, userId)
            .apply()
    }
    
    /**
     * 获取用户名
     */
    fun getUsername(context: Context): String? {
        return getPrefs(context).getString(KEY_USERNAME, null)
    }
    
    /**
     * 获取用户ID
     */
    fun getUserId(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_ID, null)
    }
    
    /**
     * 保存设备ID
     */
    fun saveDeviceId(context: Context, deviceId: String) {
        getPrefs(context).edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }
    
    /**
     * 获取设备ID
     */
    fun getDeviceId(context: Context): String? {
        return getPrefs(context).getString(KEY_DEVICE_ID, null)
    }
    
    /**
     * 清除所有用户信息
     */
    fun clearUserInfo(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USERNAME)
            .remove(KEY_USER_ID)
            .apply()
    }
    
    /**
     * 获取Authorization Header值
     */
    fun getAuthHeader(context: Context): String? {
        val token = getToken(context)
        return if (token != null) "Bearer $token" else null
    }
}





