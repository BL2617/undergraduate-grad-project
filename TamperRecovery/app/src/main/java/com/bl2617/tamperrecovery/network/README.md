# 图片接收接口使用说明

本文档说明如何使用从后端接收图片的接口。

## 配置

### 1. 修改基础URL

在 `NetworkModule.kt` 中修改 `BASE_URL` 为你的后端API地址：

```kotlin
private const val BASE_URL = "https://your-api-domain.com/"
```

### 2. 初始化网络模块（可选）

在 `Application` 或 `MainActivity` 中初始化网络模块：

```kotlin
NetworkModule.init(context = this)
```

## API接口说明

### 1. 获取单张图片信息

```kotlin
val repository = ImageRepository()
val result = repository.getImageById("image123")

result.fold(
    onSuccess = { imageData ->
        // imageData.url 是图片URL
        // imageData.width, imageData.height 等
    },
    onFailure = { exception ->
        // 处理错误
    }
)
```

### 2. 获取图片列表

```kotlin
val repository = ImageRepository()
val result = repository.getImageList(page = 1, pageSize = 20, category = "category1")

result.fold(
    onSuccess = { response ->
        val images = response.data?.images ?: emptyList()
        // 使用图片列表
    },
    onFailure = { exception ->
        // 处理错误
    }
)
```

### 3. 下载图片为Bitmap

```kotlin
val repository = ImageRepository()
val result = repository.downloadImageAsBitmap("https://example.com/image.jpg")

result.fold(
    onSuccess = { bitmap ->
        // 使用bitmap
    },
    onFailure = { exception ->
        // 处理错误
    }
)
```

### 4. 通过图片ID下载图片

```kotlin
val repository = ImageRepository()
val result = repository.downloadImageByIdAsBitmap("image123")

result.fold(
    onSuccess = { bitmap ->
        // 使用bitmap
    },
    onFailure = { exception ->
        // 处理错误
    }
)
```

## 在ViewModel中使用

```kotlin
class ImageViewModel : ViewModel() {
    private val imageRepository = ImageRepository()
    
    var images by mutableStateOf<List<ImageData>>(emptyList())
    
    fun loadImages() {
        viewModelScope.launch {
            imageRepository.getImageList().fold(
                onSuccess = { response ->
                    images = response.data?.images ?: emptyList()
                },
                onFailure = { exception ->
                    // 处理错误
                }
            )
        }
    }
}
```

## 在Compose中使用Coil加载图片

如果后端返回的是图片URL，可以直接使用Coil在Compose中加载：

```kotlin
@Composable
fun ImageItem(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier.fillMaxWidth()
    )
}
```

## 数据模型

### ImageResponse
- `code`: 响应码（200表示成功）
- `message`: 响应消息
- `data`: 图片数据（ImageData）

### ImageData
- `id`: 图片ID
- `url`: 图片URL
- `thumbnailUrl`: 缩略图URL（可选）
- `width`: 图片宽度（可选）
- `height`: 图片高度（可选）
- `size`: 图片大小（可选）
- `format`: 图片格式（可选）
- `timestamp`: 时间戳（可选）

### ImageListResponse
- `code`: 响应码
- `message`: 响应消息
- `data`: 图片列表数据（ImageListData）

### ImageListData
- `images`: 图片列表
- `total`: 总数量
- `page`: 当前页码
- `pageSize`: 每页数量

## 注意事项

1. 所有网络请求都在IO线程中执行，结果会自动返回到调用线程
2. 使用 `Result` 类型包装返回值，提供类型安全的错误处理
3. 下载大图片时使用 `@Streaming` 注解，避免内存溢出
4. 根据实际后端API的JSON结构，可能需要调整数据模型类
5. 生产环境建议关闭HTTP日志记录（通过 `NetworkModule.isDebugMode = false`）



