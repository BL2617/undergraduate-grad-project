package com.bl2617.tamperrecovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bl2617.tamperrecovery.ui.theme.*

/**
 * 科技感按钮
 */
@Composable
fun TechButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false
) {
    val gradient = if (isSecondary) {
        Brush.linearGradient(listOf(SecondaryLight, Secondary))
    } else {
        Brush.linearGradient(listOf(PrimaryLight, Primary))
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSecondary) OnSecondary else OnPrimary
            )
        }
    }
}

/**
 * 科技感卡片
 */
@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .background(Surface)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * 科技感输入框
 */
@Composable
fun TechTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = OnSurfaceVariant
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        singleLine = true,
        visualTransformation = if (isPassword) {
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceVariant,
            unfocusedContainerColor = SurfaceVariant,
            disabledContainerColor = SurfaceVariant,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurfaceVariant,
            focusedIndicatorColor = Primary,
            unfocusedIndicatorColor = SurfaceVariant,
            disabledIndicatorColor = SurfaceVariant,
            focusedLabelColor = Primary,
            unfocusedLabelColor = OnSurfaceVariant,
            disabledLabelColor = OnSurfaceVariant
        )
    )
}

/**
 * 科技感标题
 */
@Composable
fun TechTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = OnSurface,
        modifier = modifier
    )
}

/**
 * 科技感副标题
 */
@Composable
fun TechSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = OnSurfaceVariant,
        modifier = modifier
    )
}

/**
 * 科技感分隔线
 */
@Composable
fun TechDivider() {
    Divider(
        color = SurfaceVariant,
        thickness = 1.dp
    )
}

/**
 * 科技感加载指示器
 */
@Composable
fun TechLoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Primary
        )
    }
}

/**
 * 科技感错误提示
 */
@Composable
fun TechError(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "错误",
                color = OnSurface
            )
        },
        text = {
            Text(
                text = message,
                color = OnSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error
                )
            ) {
                Text(
                    text = "确定",
                    color = OnError
                )
            }
        },
        containerColor = Surface
    )
}