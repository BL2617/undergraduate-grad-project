package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bl2617.tamperrecovery.ui.components.*
import com.bl2617.tamperrecovery.ui.theme.*
import com.bl2617.tamperrecovery.viewmodel.AuthState
import com.bl2617.tamperrecovery.viewmodel.AuthViewModel

/**
 * 登录界面
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // 监听登录成功
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 应用标题
        Box(
            modifier = Modifier
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "图像篡改检测",
                style = MaterialTheme.typography.displayMedium,
                color = OnSurface,
                modifier = Modifier
//                    .background(
//                        brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
//                        alpha = 0.1f
//                    )
                    .padding(16.dp)
            )
        }
        
        // 登录/注册卡片
        TechCard(
            modifier = Modifier
                .width(620.dp)
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 标题
                TechTitle(
                    text = if (isLoginMode) "登录" else "注册",
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                )
                
                // 用户名输入框
                TechTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "用户名",
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
                
                // 邮箱输入框（仅注册时显示）
                if (!isLoginMode) {
                    TechTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "邮箱",
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    )
                }
                
                // 密码输入框
                TechTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "密码",
                    isPassword = !passwordVisible,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
                
                // 显示/隐藏密码按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Text(
                            text = if (passwordVisible) "隐藏密码" else "显示密码",
                            color = Primary
                        )
                    }
                }
                
                // 错误消息
                if (authState is AuthState.Error) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
                
                // 登录/注册按钮
                TechButton(
                    text = if (isLoginMode) "登录" else "注册",
                    onClick = {
                        if (isLoginMode) {
                            viewModel.login(username, password)
                        } else {
                            viewModel.register(username, email, password)
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    enabled = authState !is AuthState.Loading && 
                             username.isNotBlank() && 
                             password.isNotBlank() &&
                             (isLoginMode || email.isNotBlank())
                )
                
                // 加载指示器
                if (authState is AuthState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Primary
                        )
                    }
                }
            }
        }
        
        // 切换登录/注册模式
        TextButton(
            onClick = { 
                isLoginMode = !isLoginMode
                // 清除错误状态
                if (authState is AuthState.Error) {
                    // 可以通过重新创建 ViewModel 或添加清除方法
                }
            }
        ) {
            Text(
                text = if (isLoginMode) "还没有账号？注册" else "已有账号？登录",
                color = Primary
            )
        }
    }
}
