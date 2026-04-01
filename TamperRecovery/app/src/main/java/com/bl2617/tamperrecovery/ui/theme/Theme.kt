package com.bl2617.tamperrecovery.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    primaryContainer = PrimaryDark,
    secondary = Secondary,
    secondaryContainer = SecondaryDark,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    onError = OnError
)

private val LightColorScheme = darkColorScheme(
    primary = Primary,
    primaryContainer = PrimaryDark,
    secondary = Secondary,
    secondaryContainer = SecondaryDark,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    onError = OnError
)

// 强制使用暗色主题，因为科技感设计更适合深色背景
private val TechColorScheme = DarkColorScheme

@Composable
fun TamperRecoveryTheme(
    content: @Composable () -> Unit
) {
    // 强制使用科技感主题
    val colorScheme = TechColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}