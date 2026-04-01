package com.bl2617.tamperrecovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import com.bl2617.tamperrecovery.ui.theme.*
import com.bl2617.tamperrecovery.utils.AuthManager
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
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 检测中心, 1: 检测历史, 2: 个人中心
    var selectedImageId by remember { mutableStateOf<String?>(null) }
    var detectionResult by remember { mutableStateOf<DetectionResultData?>(null) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // 当退出登录时，清除所有状态
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            selectedImageId = null
            detectionResult = null
            selectedImageUri = null
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

        // 主界面：使用上下布局，顶部显示内容，底部显示导航栏
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部内容区域
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                imageViewModel?.let { imgViewModel ->
                    detectionViewModel?.let { detViewModel ->
                        when {
                            selectedTab == 1 -> {
                                DetectionHistoryScreen(
                                    viewModel = detViewModel,
                                    onBack = {
                                        // 不需要返回，因为这是标签页
                                    },
                                    onViewResult = { result ->
                                        detectionResult = result
                                        selectedTab = 0 // 切换到检测中心标签页显示结果
                                    }
                                )
                            }

                            selectedTab == 0 -> {
                                // 篡改检测
                                if (detectionResult != null) {
                                    DetectionResultScreen(
                                        result = detectionResult!!,
                                        originalImageUri = selectedImageUri,
                                        viewModel = detViewModel,
                                        onBack = {
                                            detectionResult = null
                                            selectedImageUri = null
                                        }
                                    )
                                } else {
                                    DetectionScreen(
//                                        viewModel = detViewModel,
                                        onStartDetection = { uri ->
                                            selectedImageUri = uri
                                            // 同时执行水印检测和模型检测
                                            detViewModel.detectLSBWatermark(uri) { lsbResult ->
                                                // 处理LSB检测结果
                                                detectionResult = lsbResult
                                            }
                                            detViewModel.detectWithModel(uri) { modelResult ->
                                                // 处理模型检测结果
                                                detectionResult = modelResult
                                            }
                                        }
                                    )
                                }
                            }

                            selectedTab == 2 -> {
                                // 个人中心
                                ProfileScreen(
                                    authViewModel = authViewModel,
                                    onLogout = {
                                        imageViewModel?.clearAllState()
                                        detectionViewModel?.clearAllState()
                                        authViewModel.logout()
                                        isLoggedIn = false
                                    }
                                )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {

                    Spacer(modifier = Modifier.weight(1f))
                    NavigateBarItem(
                        imageVector = Icons.Default.Security,
                        text = "检测中心",
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(30.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NavigateBarItem(
                        imageVector = Icons.Default.Image,
                        text = "检测历史",
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(30.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NavigateBarItem(
                        imageVector = Icons.Default.Person,
                        text = "个人中心",
                        isSelected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(30.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun NavigateBarItem(
    imageVector: ImageVector,
    text: String,
    isSelected: Boolean = false,
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
        Icon(
            imageVector = imageVector,
            contentDescription = text,
            tint = if (isSelected) Primary else OnSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(spacerWidth))
        Text(
            text = text,
            color = if (isSelected) Primary else OnSurfaceVariant
        )
    }
}
