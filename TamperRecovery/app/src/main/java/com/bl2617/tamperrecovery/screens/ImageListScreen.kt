package com.bl2617.tamperrecovery.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.ui.components.*
import com.bl2617.tamperrecovery.ui.theme.*
import com.bl2617.tamperrecovery.utils.AuthManager
import com.bl2617.tamperrecovery.viewmodel.ImageListState
import com.bl2617.tamperrecovery.viewmodel.ImageViewModel
import com.bl2617.tamperrecovery.viewmodel.UploadState
import java.io.File

/**
 * 图片列表界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(
    viewModel: ImageViewModel,
    onImageClick: (ImageData) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageListState by viewModel.imageListState.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // 用户菜单状态
    var showUserMenu by remember { mutableStateOf(false) }

    // 获取当前用户名
    val currentUsername = remember { AuthManager.getUsername(context) ?: "用户" }

    // Snackbar 用于显示上传结果
    val snackbarHostState = remember { SnackbarHostState() }

    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                // 将 URI 转换为 File
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }
                inputStream?.close()

                // 上传图片
                viewModel.uploadImage(file)
            } catch (e: Exception) {
                // 错误处理在 ViewModel 中
            }
        }
    }

    // 监听上传状态
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadState.Success -> {
                snackbarHostState.showSnackbar("上传成功")
            }

            is UploadState.Error -> {
                snackbarHostState.showSnackbar("上传失败: ${(uploadState as UploadState.Error).message}")
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "图像管理",
                        color = OnSurface
                    )
                },
                actions = {
                    // 刷新按钮
                    IconButton(
                        onClick = { viewModel.refreshImageList() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = OnSurface,
                    actionIconContentColor = OnSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.padding(16.dp),
                containerColor = Primary,
                contentColor = OnPrimary
            ) {
                if (uploadState is UploadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = OnPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "上传图片"
                    )
                }
            }
        },
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        modifier = Modifier
                            .background(Surface)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = it.visuals.message,
                            color = OnSurface
                        )
                    }
                }
            ) 
        },
        containerColor = Background
    ) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            // 内容区域
            when (val state = imageListState) {
                is ImageListState.Loading -> {
                    TechLoadingIndicator()
                }

                is ImageListState.Success -> {
                    if (state.images.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TechSubtitle(
                                    text = "暂无图片",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                TechSubtitle(
                                    text = "点击右下角的 + 按钮上传图片"
                                )
                            }
                        }
                    } else {
                        val gridState = rememberLazyGridState()

                        // 监听滚动，自动加载更多
                        LaunchedEffect(gridState) {
                            snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                .collect { lastVisibleIndex ->
                                    if (lastVisibleIndex != null &&
                                        lastVisibleIndex >= state.images.size - 3 &&
                                        state.hasMore
                                    ) {
                                        viewModel.loadMoreImages()
                                    }
                                }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = gridState,
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.images) { image ->
                                ImageItem(
                                    image = image,
                                    onClick = { onImageClick(image) }
                                )
                            }

                            // 加载更多指示器
                            if (state.hasMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is ImageListState.Error -> {
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
                                color = Error
                            )
                            TechButton(
                                text = "重试",
                                onClick = { viewModel.refreshImageList() }
                            )
                        }
                    }
                }

                is ImageListState.LoadingMore -> {
                    // 加载更多时显示加载指示器
                    TechLoadingIndicator()
                }
            }
        }
    }
}

/**
 * 图片项组件
 */
@Composable
fun ImageItem(
    image: ImageData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TechCard(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Box {
            // 图片
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image.thumbnailUrl ?: image.url)
                    .crossfade(true)
                    .build(),
                contentDescription = image.id,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 底部信息栏
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Surface.copy(alpha = 0.8f))
                    .padding(8.dp)
            ) {
                // 图片尺寸
                if (image.width != null && image.height != null) {
                    Text(
                        text = "${image.width} × ${image.height}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = OnSurface
                    )
                }

                // 文件大小
                if (image.size != null) {
                    Text(
                        text = formatFileSize(image.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}
