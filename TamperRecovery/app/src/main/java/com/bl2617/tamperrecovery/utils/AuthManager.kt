package com.bl2617.tamperrecovery.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 认证管理器
 * 负责管理用户认证 token 的存储和获取
 */
object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_USERNAME = "username"
    
    /**
     * 保存 token
     */
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    /**
     * 获取 token
     */
    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }
    
    /**
     * 保存用户名
     */
    fun saveUsername(context: Context, username: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }
    
    /**
     * 获取用户名
     */
    fun getUsername(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USERNAME, null)
    }
    
    /**
     * 清除所有认证信息
     */
    fun clearAuth(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(context: Context): Boolean {
        return getToken(context) != null
    }
}
