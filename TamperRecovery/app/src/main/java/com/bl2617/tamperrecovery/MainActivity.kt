package com.bl2617.tamperrecovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.screens.ImageDetailScreen
import com.bl2617.tamperrecovery.screens.ImageListScreen
import com.bl2617.tamperrecovery.screens.LoginScreen
import com.bl2617.tamperrecovery.ui.theme.TamperRecoveryTheme
import com.bl2617.tamperrecovery.utils.AuthManager
import com.bl2617.tamperrecovery.viewmodel.AuthViewModel
import com.bl2617.tamperrecovery.viewmodel.ImageViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化网络模块
        NetworkModule.init(this)
        
        enableEdgeToEdge()
        setContent {
            TamperRecoveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TamperRecoveryApp()
                }
            }
        }
    }
}

@Composable
fun TamperRecoveryApp() {
    val context = LocalContext.current
    
    // 检查登录状态
    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn(context)) }
    
    // 创建 ViewModel
    val authViewModel = remember { AuthViewModel(context) }
    
    // 使用 key 确保每次登录状态改变时都创建新的 ImageViewModel
    val imageViewModel = remember(isLoggedIn) {
        if (isLoggedIn) {
            ImageViewModel(context)
        } else {
            null
        }
    }
    
    var selectedImageId by remember { mutableStateOf<String?>(null) }
    
    // 当退出登录时，清除选中的图片ID
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            selectedImageId = null
        }
    }
    
    // 根据登录状态显示不同界面
    if (!isLoggedIn) {
        // 显示登录界面
        LoginScreen(
            viewModel = authViewModel,
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
    } else {
        // 显示主界面
        imageViewModel?.let { viewModel ->
            when {
                selectedImageId != null -> {
                    ImageDetailScreen(
                        imageId = selectedImageId!!,
                        viewModel = viewModel,
                        onBack = { selectedImageId = null },
                        onLogout = {
                            // 退出登录：清除 ViewModel 状态、认证信息并返回登录界面
                            viewModel.clearAllState()
                            authViewModel.logout()
                            isLoggedIn = false
                        }
                    )
                }
                else -> {
                    ImageListScreen(
                        viewModel = viewModel,
                        onImageClick = { image: ImageData ->
                            selectedImageId = image.id
                        },
                        onLogout = {
                            // 退出登录：清除 ViewModel 状态、认证信息并返回登录界面
                            viewModel.clearAllState()
                            authViewModel.logout()
                            isLoggedIn = false
                        }
                    )
                }
            }
        }
    }
}

