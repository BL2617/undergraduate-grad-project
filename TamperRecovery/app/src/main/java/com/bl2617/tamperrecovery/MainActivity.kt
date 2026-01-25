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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.screens.ImageDetailScreen
import com.bl2617.tamperrecovery.screens.ImageListScreen
import com.bl2617.tamperrecovery.screens.LoginScreen
import com.bl2617.tamperrecovery.ui.theme.TamperRecoveryTheme
import com.bl2617.tamperrecovery.viewmodel.AuthState
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val authViewModel = remember { AuthViewModel(context) }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    
    when (authState) {
        is AuthState.Authenticated -> {
            // 已登录，显示主界面
            MainApp(authViewModel)
        }
        is AuthState.Unauthenticated -> {
            // 未登录，显示登录界面
            LoginScreen(viewModel = authViewModel)
        }
    }
}

@Composable
fun MainApp(authViewModel: AuthViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    
    // 当认证状态改变时，重新创建 ImageViewModel 以获取最新的 Token
    val imageViewModel = remember(authState) { 
        ImageViewModel(context)
    }
    var selectedImageId by remember { mutableStateOf<String?>(null) }
    
    when {
        selectedImageId != null -> {
            ImageDetailScreen(
                imageId = selectedImageId!!,
                viewModel = imageViewModel,
                onBack = { selectedImageId = null }
            )
        }
        else -> {
            ImageListScreen(
                viewModel = imageViewModel,
                onImageClick = { image: ImageData ->
                    selectedImageId = image.id
                }
            )
        }
    }
}

