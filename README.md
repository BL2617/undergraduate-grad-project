# 图片传输恢复 Android 客户端

这是一个完整的图片浏览 Android 应用，与后端 API 配合使用，可以浏览、查看和下载图片。

## 功能特性

- ✅ 图片列表展示（网格布局）
- ✅ 图片详情查看
- ✅ 自动加载更多
- ✅ 分类筛选
- ✅ 下拉刷新
- ✅ 缩略图加载优化
- ✅ 错误处理和重试

## 项目结构

```
TamperRecovery/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/bl2617/tamperrecovery/
│           │   ├── MainActivity.kt              # 主Activity
│           │   ├── data/
│           │   │   ├── model/                  # 数据模型
│           │   │   │   └── ImageResponse.kt
│           │   │   └── repository/             # 数据仓库
│           │   │       └── ImageRepository.kt
│           │   ├── network/                     # 网络层
│           │   │   ├── ApiService.kt
│           │   │   └── NetworkModule.kt
│           │   ├── screens/                     # UI界面
│           │   │   ├── ImageListScreen.kt
│           │   │   └── ImageDetailScreen.kt
│           │   └── viewmodel/                   # ViewModel
│           │       └── ImageViewModel.kt
│           └── AndroidManifest.xml
```

## 配置说明

### 1. 配置后端服务器地址

在 `NetworkModule.kt` 中修改 `BASE_URL`：

```kotlin
// Android 模拟器访问本地主机
private const val BASE_URL = "http://10.0.2.2:8000/"

// 真机测试（替换为你的电脑IP地址）
// private const val BASE_URL = "http://192.168.1.100:8000/"
```

**注意：**
- Android 模拟器使用 `10.0.2.2` 访问本地主机
- 真机测试需要将 `10.0.2.2` 替换为你的电脑在局域网中的 IP 地址
- 确保手机和电脑在同一 WiFi 网络下

### 2. 获取电脑 IP 地址

**Windows:**
```cmd
ipconfig
```
查找 "IPv4 地址"，例如：`192.168.1.100`

**Mac/Linux:**
```bash
ifconfig
```
查找 `inet` 地址

## 运行步骤

### 1. 启动后端服务

```bash
cd ImageTamperRecovery_Backend
python main.py
```

后端服务将在 `http://localhost:8000` 启动。

### 2. 上传测试图片（可选）

在另一个终端运行：
```bash
cd ImageTamperRecovery_Backend
python upload_image.py
```

然后使用 curl 或 Postman 上传图片：
```bash
curl -X POST "http://localhost:8001/api/upload" \
  -F "file=@your_image.jpg" \
  -F "category=测试"
```

### 3. 运行 Android 应用

1. 使用 Android Studio 打开项目
2. 连接 Android 设备或启动模拟器
3. 修改 `NetworkModule.kt` 中的 `BASE_URL`（如果需要）
4. 点击运行按钮

## 使用说明

### 图片列表界面

- **浏览图片**：以网格形式展示所有图片
- **下拉刷新**：点击顶部刷新按钮或下拉列表刷新
- **自动加载更多**：滚动到底部自动加载更多图片
- **分类筛选**：点击筛选按钮按分类筛选图片
- **查看详情**：点击任意图片查看详情

### 图片详情界面

- **查看大图**：显示完整尺寸的图片
- **查看信息**：显示图片的ID、尺寸、大小、格式等信息
- **返回列表**：点击返回按钮返回列表

## 技术栈

- **UI框架**：Jetpack Compose
- **架构模式**：MVVM
- **网络请求**：Retrofit + OkHttp
- **图片加载**：Coil
- **异步处理**：Kotlin Coroutines
- **状态管理**：StateFlow

## 依赖库

主要依赖已在 `build.gradle.kts` 中配置：

- Retrofit 2.9.0
- OkHttp 4.12.0
- Coil 2.6.0
- Jetpack Compose
- Lifecycle ViewModel

## 常见问题

### 1. 无法连接到服务器

**问题**：应用显示"网络请求失败"

**解决方案**：
- 检查后端服务是否已启动
- 检查 `BASE_URL` 配置是否正确
- 真机测试时，确保手机和电脑在同一 WiFi 网络
- 检查防火墙设置

### 2. 图片无法加载

**问题**：图片显示空白或加载失败

**解决方案**：
- 检查后端服务是否正常运行
- 检查图片URL是否正确
- 查看 Logcat 中的错误信息

### 3. Android 模拟器无法访问本地服务器

**问题**：模拟器无法连接到 `localhost:8000`

**解决方案**：
- 使用 `10.0.2.2` 代替 `localhost` 或 `127.0.0.1`
- 确保后端服务绑定到 `0.0.0.0` 而不是 `127.0.0.1`

## 开发说明

### 添加新功能

1. **添加新的API接口**：在 `ApiService.kt` 中定义
2. **添加数据模型**：在 `data/model/` 中创建
3. **添加Repository方法**：在 `ImageRepository.kt` 中实现
4. **添加ViewModel逻辑**：在 `ImageViewModel.kt` 中处理
5. **创建UI界面**：在 `screens/` 中创建新的 Compose 函数

### 调试

- 查看网络请求日志：`NetworkModule.kt` 中已配置日志拦截器
- 查看应用日志：使用 Android Studio 的 Logcat
- 测试API：可以使用 Postman 或 curl 测试后端接口

## 许可证

MIT License


