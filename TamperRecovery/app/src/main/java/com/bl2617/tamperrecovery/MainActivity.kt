package com.bl2617.tamperrecovery

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bl2617.tamperrecovery.ui.theme.TamperRecoveryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TamperRecoveryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TamperRecoveryApp()
                }
            }
        }
    }
}

@Composable
fun TamperRecoveryApp(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier.background(color = Color(0xAA30AAAA))
                .fillMaxWidth()
        ) {
            Text("Title", modifier = Modifier)
        }

        Column(modifier =
            Modifier.background(color = Color(0x806AAAAA))
                .width(40.dp)
                .fillMaxHeight()

        ) {
        }
    }
}


@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun TamperRecoryAppPreview() {
    TamperRecoveryApp()
}

