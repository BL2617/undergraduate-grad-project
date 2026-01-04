package com.bl2617.tamperrecovery.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
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
    modifier: Modifier = Modifier
) {
    val imageListState by viewModel.imageListState.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 将 URI 转换为 File
            val inputStream = context.contentResolver.openInputStream(it)
            val fileName = getFileName(context, it) ?: "image_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, fileName)
            
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            // 上传文件
            viewModel.uploadImage(file, selectedCategory)
        }
    }
    
    // 显示上传状态
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadState.Success -> {
                viewModel.clearUploadState()
            }
            is UploadState.Error -> {
                // 错误信息可以通过 Snackbar 显示
            }
            else -> {}
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("图片列表") },
            actions = {
                // 上传按钮
                IconButton(onClick = { 
                    imagePickerLauncher.launch("image/*")
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "上传图片"
                    )
                }
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
        
        // 上传状态提示
        if (uploadState is UploadState.Uploading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        
        // 上传错误提示
        if (uploadState is UploadState.Error) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (uploadState as UploadState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    TextButton(onClick = { viewModel.clearUploadState() }) {
                        Text("关闭")
                    }
                }
            }
        }
        
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
                        Text("暂无图片")
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

/**
 * 从 URI 获取文件名
 */
private fun getFileName(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}

