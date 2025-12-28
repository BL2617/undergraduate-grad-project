package com.bl2617.tamperrecovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bl2617.tamperrecovery.data.model.ImageData
import com.bl2617.tamperrecovery.network.NetworkModule
import com.bl2617.tamperrecovery.screens.ImageDetailScreen
import com.bl2617.tamperrecovery.screens.ImageListScreen
import com.bl2617.tamperrecovery.ui.theme.TamperRecoveryTheme
import com.bl2617.tamperrecovery.viewmodel.ImageViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化网络模块
        NetworkModule.init(this)
        
        enableEdgeToEdge()
        setContent {
            TamperRecoveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TamperRecoveryApp()
                }
            }
        }
    }
}

@Composable
fun TamperRecoveryApp(
    viewModel: ImageViewModel = viewModel()
) {
    var selectedImageId by remember { mutableStateOf<String?>(null) }
    
    when {
        selectedImageId != null -> {
            ImageDetailScreen(
                imageId = selectedImageId!!,
                viewModel = viewModel,
                onBack = { selectedImageId = null }
            )
        }
        else -> {
            ImageListScreen(
                viewModel = viewModel,
                onImageClick = { image: ImageData ->
                    selectedImageId = image.id
                }
            )
        }
    }
}

