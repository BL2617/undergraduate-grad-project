package com.bl2617.tamperrecovery.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bl2617.tamperrecovery.ui.components.*
import com.bl2617.tamperrecovery.ui.theme.*
import com.bl2617.tamperrecovery.utils.AuthManager
import com.bl2617.tamperrecovery.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val username = AuthManager.getUsername(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 个人信息卡片
        TechCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 用户头像占位符
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Primary,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = username?.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 用户名和邮箱
                Text(
                    text = username ?: "未登录",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurface
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        // 功能列表
        TechCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 账号设置
                ListItem(
                    headlineContent = { 
                        Text(
                            text = "账号设置",
                            color = OnSurface
                        ) 
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "账号设置",
                            tint = Primary
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = SurfaceVariant
                    )
                )

                TechDivider()

                // 关于应用
                ListItem(
                    headlineContent = { 
                        Text(
                            text = "关于应用",
                            color = OnSurface
                        ) 
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "关于应用",
                            tint = Primary
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = SurfaceVariant
                    )
                )

                TechDivider()

                // 帮助与反馈
                ListItem(
                    headlineContent = { 
                        Text(
                            text = "帮助与反馈",
                            color = OnSurface
                        ) 
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "帮助与反馈",
                            tint = Primary
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = SurfaceVariant
                    )
                )

                TechDivider()

                // 退出登录按钮
                TechButton(
                    text = "退出登录",
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    isSecondary = true
                )
            }
        }
    }
}