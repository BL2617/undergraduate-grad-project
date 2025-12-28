# 快速启动指南

## 前置条件

1. Python 3.7+ 已安装
2. Android Studio 已安装
3. Android 设备或模拟器

## 快速开始（5分钟）

### 步骤 1: 启动后端服务

```bash
# 进入后端目录
cd ../ImageTamperRecovery_Backend

# 安装依赖（首次运行）
pip install -r requirements.txt

# 启动服务
python main.py
```

看到以下输出表示启动成功：
```
INFO:     Uvicorn running on http://0.0.0.0:8000
```

### 步骤 2: 上传测试图片（可选）

在另一个终端运行：
```bash
cd ImageTamperRecovery_Backend
python upload_image.py
```

然后上传图片：
```bash
curl -X POST "http://localhost:8001/api/upload" \
  -F "file=@test_image.jpg" \
  -F "category=测试"
```

### 步骤 3: 配置 Android 应用

1. 打开 Android Studio
2. 打开项目：`ImageTamperRecovery_Android/TamperRecovery`
3. 检查 `NetworkModule.kt` 中的 `BASE_URL`：
   - 模拟器：`http://10.0.2.2:8000/`（默认已配置）
   - 真机：替换为你的电脑IP，如 `http://192.168.1.100:8000/`

### 步骤 4: 运行 Android 应用

1. 连接设备或启动模拟器
2. 点击运行按钮（绿色三角形）
3. 等待应用安装和启动

## 测试检查清单

- [ ] 后端服务正常运行（访问 http://localhost:8000/docs 查看API文档）
- [ ] 至少上传了一张测试图片
- [ ] Android 应用成功启动
- [ ] 图片列表正常显示
- [ ] 可以点击图片查看详情
- [ ] 可以滚动加载更多图片

## 常见问题快速解决

### 问题：应用显示"网络请求失败"

**解决**：
1. 确认后端服务正在运行
2. 检查 `BASE_URL` 配置
3. 真机测试时，确保手机和电脑在同一WiFi

### 问题：图片列表为空

**解决**：
1. 使用上传服务上传一些测试图片
2. 检查后端数据库是否有数据

### 问题：Android 模拟器无法连接

**解决**：
- 使用 `10.0.2.2` 而不是 `localhost`
- 确保后端绑定到 `0.0.0.0` 而不是 `127.0.0.1`

## 下一步

- 查看完整文档：`README.md`
- 自定义UI：修改 `screens/` 中的文件
- 添加功能：参考现有代码结构

## 获取帮助

- 查看后端日志：终端输出
- 查看Android日志：Android Studio Logcat
- 测试API：访问 http://localhost:8000/docs


