package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.utils.AuthManager
import com.bl2617.tamperrecovery.viewmodel.ImageDetailState
import com.bl2617.tamperrecovery.viewmodel.ImageViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 图片详情界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    imageId: String,
    viewModel: ImageViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageDetailState by viewModel.imageDetailState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // 用户菜单状态
    var showUserMenu by remember { mutableStateOf(false) }
    
    // 获取当前用户名
    val currentUsername = remember { AuthManager.getUsername(context) ?: "用户" }
    
    LaunchedEffect(imageId) {
        viewModel.loadImageDetail(imageId)
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("图片详情") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "返回"
                    )
                }
            },
            actions = {
                // 用户菜单按钮
                Box {
                    IconButton(onClick = { showUserMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "用户菜单"
                        )
                    }
                    DropdownMenu(
                        expanded = showUserMenu,
                        onDismissRequest = { showUserMenu = false }
                    ) {
                        // 显示当前用户名
                        DropdownMenuItem(
                            text = { 
                                Column {
                                    Text(
                                        text = currentUsername,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "已登录",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            onClick = { showUserMenu = false },
                            enabled = false
                        )
                        Divider()
                        // 切换账号
                        DropdownMenuItem(
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SwapHoriz,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("切换账号")
                                }
                            },
                            onClick = {
                                showUserMenu = false
                                onLogout()
                            }
                        )
                        // 退出登录
                        DropdownMenuItem(
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("退出登录")
                                }
                            },
                            onClick = {
                                showUserMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        )
        
        // 内容区域
        when (val state = imageDetailState) {
            is ImageDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is ImageDetailState.Success -> {
                ImageDetailContent(
                    image = state.image,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            is ImageDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadImageDetail(imageId) }) {
                            Text("重试")
                        }
                    }
                }
            }
            
            is ImageDetailState.Idle -> {
                // 初始状态，不显示内容
            }
        }
    }
}

/**
 * 图片详情内容
 */
@Composable
fun ImageDetailContent(
    image: ImageData,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // 创建带认证的 ImageLoader
    val imageLoader = remember {
        val token = AuthManager.getToken(context)
        val okHttpClient = NetworkModule.createAuthenticatedOkHttpClient(token)
        ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 图片
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image.url)
                .crossfade(true)
                .build(),
            contentDescription = image.id,
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    if (image.width != null && image.height != null && image.height != 0) {
                        image.width.toFloat() / image.height.toFloat()
                    } else {
                        1f
                    }
                ),
            contentScale = ContentScale.Fit
        )
        
        // 图片信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ID
                if (image.id != null) {
                    InfoRow("ID", image.id)
                }
                
                // 尺寸
                if (image.width != null && image.height != null) {
                    InfoRow("尺寸", "${image.width} × ${image.height}")
                }
                
                // 文件大小
                if (image.size != null) {
                    InfoRow("文件大小", formatFileSize(image.size))
                }
                
                // 格式
                if (image.format != null) {
                    InfoRow("格式", image.format.uppercase())
                }
                
                // 时间戳
                if (image.timestamp != null) {
                    InfoRow(
                        "上传时间",
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(Date(image.timestamp))
                    )
                }
                
                // URL
                InfoRow("URL", image.url, isUrl = true)
            }
        }
    }
}

/**
 * 信息行组件
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    isUrl: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f),
            maxLines = if (isUrl) 3 else 1
        )
    }
}

/**
 * 格式化文件大小
 */
fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.2f GB", gb)
        mb >= 1 -> String.format("%.2f MB", mb)
        kb >= 1 -> String.format("%.2f KB", kb)
        else -> "$bytes B"
    }
}



