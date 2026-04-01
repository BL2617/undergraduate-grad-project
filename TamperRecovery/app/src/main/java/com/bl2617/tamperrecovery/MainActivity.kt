package com.bl2617.tamperrecovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bl2617.tamperrecovery.data.model.DetectionResultData
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.screens.*
import com.bl2617.tamperrecovery.ui.theme.TamperRecoveryTheme
import com.bl2617.tamperrecovery.utils.AuthManager
import com.bl2617.tamperrecovery.utils.LogUtil
import com.bl2617.tamperrecovery.viewmodel.AuthViewModel
import com.bl2617.tamperrecovery.viewmodel.DetectionViewModel
import com.bl2617.tamperrecovery.viewmodel.ImageViewModel

val bottomBarItemWidth = 120.dp

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
                    Scaffold { contentPadding ->
                        Box(modifier = Modifier.padding(contentPadding)) {
                            TamperRecoveryApp()
                        }
                    }
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

    // 使用 key 确保每次登录状态改变时都创建新的 ViewModel
    val imageViewModel = remember(isLoggedIn) {
        if (isLoggedIn) {
            ImageViewModel(context)
        } else {
            null
        }
    }

    val detectionViewModel = remember(isLoggedIn) {
        if (isLoggedIn) {
            DetectionViewModel(context)
        } else {
            null
        }
    }

    // 导航状态
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 图片管理, 1: 检测功能, 2: 个人中心
    var selectedImageId by remember { mutableStateOf<String?>(null) }
    var detectionScreen by remember { mutableStateOf<DetectionScreen?>(null) }
    var detectionResult by remember { mutableStateOf<DetectionResultData?>(null) }

    // 当退出登录时，清除所有状态
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            selectedImageId = null
            detectionScreen = null
            detectionResult = null
            selectedTab = 0
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
        imageViewModel?.let { imgViewModel ->
            detectionViewModel?.let { detViewModel ->
                // 如果有检测结果，显示结果界面（优先显示）
                // 检测功能子界面
                when (detectionScreen) {
                    DetectionScreen.Main -> {
                        DetectionMainScreen(
                            onLSBClick = { detectionScreen = DetectionScreen.LSB },
                            onModelClick = { detectionScreen = DetectionScreen.Model },
                            onBack = {
                                detectionScreen = null
                                selectedTab = 0  // 返回时切换到图片管理
                            }
                        )
                    }

                    DetectionScreen.LSB -> {
                        LSBDetectionScreen(
                            viewModel = detViewModel,
                            onBack = { detectionScreen = DetectionScreen.Main },
                            onResult = { result ->
                                detectionResult = result
                                detectionScreen = null
                            }
                        )
                    }

                    DetectionScreen.Model -> {
                        ModelDetectionScreen(
                            viewModel = detViewModel,
                            onBack = { detectionScreen = DetectionScreen.Main },
                            onResult = { result ->
                                detectionResult = result
                                detectionScreen = null
                            }
                        )
                    }

                    null -> {
                            // 主界面：使用上下布局，顶部显示内容，底部显示导航栏
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // 顶部内容区域
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    when {
                                        selectedImageId != null -> {
                                            ImageDetailScreen(
                                                imageId = selectedImageId!!,
                                                viewModel = imgViewModel,
                                                onBack = { selectedImageId = null },
                                                onLogout = {
                                                    imgViewModel.clearAllState()
                                                    detViewModel.clearAllState()
                                                    authViewModel.logout()
                                                    isLoggedIn = false
                                                }
                                            )
                                        }

                                        selectedTab == 0 -> {
                                            // 图片管理模块
                                            ImageListScreen(
                                                viewModel = imgViewModel,
                                                onImageClick = { image: ImageData ->
                                                    selectedImageId = image.id
                                                },
                                                onLogout = {
                                                    imgViewModel.clearAllState()
                                                    detViewModel.clearAllState()
                                                    authViewModel.logout()
                                                    isLoggedIn = false
                                                }
                                            )
                                        }

                                        selectedTab == 2 -> {
                                            // 个人中心
                                            ProfileScreen(
                                                authViewModel = authViewModel,
                                                onLogout = {
                                                    imgViewModel.clearAllState()
                                                    detViewModel.clearAllState()
                                                    authViewModel.logout()
                                                    isLoggedIn = false
                                                }
                                            )
                                        }

                                        else -> {
                                            // 当 selectedTab == 1 但 detectionScreen == null 时，自动设置 detectionScreen = Main
                                            // 这确保用户点击"检测功能"时能正确显示检测主界面
                                            LaunchedEffect(selectedTab) {
                                                if (selectedTab == 1 && detectionScreen == null) {
                                                    detectionScreen = DetectionScreen.Main
                                                }
                                            }
                                        }
                                    }
                                }

                                // 底部导航栏
                                val scrollState = rememberScrollState()
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
//                                        .horizontalScroll(scrollState)
                                ) {
                                     Row(
                                         modifier = Modifier.fillMaxWidth()
                                             .padding(vertical = 20.dp)
                                     ) {
                                        // 图片管理
                                         Spacer(modifier = Modifier.weight(1f))
                                         NavigateBarItem(
                                             imageVector = Icons.Default.Image,
                                             onClick = {
                                                 selectedTab = 0
                                                 LogUtil.d(msg = "图片管理")
                                                 detectionScreen = null  // 确保清除检测屏幕状态
                                             },
                                            text = "图片管理",
                                             modifier = Modifier.wrapContentSize()
                                                 .padding(30.dp)
                                         )
                                         Spacer(modifier = Modifier.weight(1f))
                                         NavigateBarItem(
                                             imageVector = Icons.Default.Security,
                                             onClick = {
                                                 selectedTab = 1
                                                 LogUtil.d(msg = "检测功能")
                                                 detectionScreen = null  // 确保清除检测屏幕状态
                                             },
                                             text = "检测功能",
                                                     modifier = Modifier.wrapContentSize()
                                                 .padding(30.dp)
                                         )
                                         Spacer(modifier = Modifier.weight(1f))
                                         NavigateBarItem(
                                             imageVector = Icons.Default.Person,
                                             onClick = {
                                                 selectedTab = 2
                                                 LogUtil.d(msg = "个人中心")
                                                 detectionScreen = null  // 确保清除检测屏幕状态
                                             },
                                             text = "个人中心",
                                             modifier = Modifier.wrapContentSize()
                                                 .padding(30.dp)
                                         )
                                         Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun NavigateBarItem(
    imageVector: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    spacerWidth: Dp = 20.dp,
    onClick: () -> Unit = {  }
) {
     Row (
        modifier = modifier

            .clickable(
            enabled = true,
            onClick = onClick
        )
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = text
        )
        Spacer(modifier = Modifier.width(spacerWidth))
        Text(text = text)
    }
}

/**
 * 检测功能子界面枚举
 */
enum class DetectionScreen {
    Main,
    LSB,
    Model
}
