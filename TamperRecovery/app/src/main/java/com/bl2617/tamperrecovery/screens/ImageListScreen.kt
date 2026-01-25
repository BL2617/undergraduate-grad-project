package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.viewmodel.ImageListState
import com.bl2617.tamperrecovery.viewmodel.ImageViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bl2617.tamperrecovery.viewmodel.UploadState

/**
 * 图片列表界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(
    viewModel: ImageViewModel,
    onImageClick: (ImageData) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageListState by viewModel.imageListState.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 选择图片
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadImage(uri = uri)
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("图片列表") },
            actions = {
                // 分类筛选按钮
                TextButton(onClick = { 
                    selectedCategory = if (selectedCategory == null) "测试" else null
                    viewModel.filterByCategory(selectedCategory)
                }) {
                    Text(if (selectedCategory != null) "取消筛选" else "筛选")
                }
                // 刷新按钮
                IconButton(onClick = { viewModel.refreshImageList() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "刷新"
                    )
                }
            }
        )
        
        // 内容区域
        when (val state = imageListState) {
            is ImageListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is ImageListState.Success -> {
                if (state.images.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("暂无图片")
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { picker.launch("image/*") }) {
                                Icon(Icons.Default.Upload, contentDescription = "上传")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("上传图片")
                            }
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
                                    state.hasMore) {
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
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.refreshImageList() }) {
                            Text("重试")
                        }
                    }
                }
            }
            
            is ImageListState.LoadingMore -> {
                // 加载更多时显示加载指示器
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // 上传浮动按钮
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (uploadState is UploadState.Uploading) {
                ExtendedFloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    icon = { CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp) },
                    text = { Text("正在上传...") },
                    expanded = true
                )
            } else {
                FloatingActionButton(
                    onClick = { picker.launch("image/*") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Upload, contentDescription = "上传图片")
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .padding(8.dp)
            ) {
                // 图片尺寸
                if (image.width != null && image.height != null) {
                    Text(
                        text = "${image.width} × ${image.height}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // 文件大小
                if (image.size != null) {
                    Text(
                        text = formatFileSize(image.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

