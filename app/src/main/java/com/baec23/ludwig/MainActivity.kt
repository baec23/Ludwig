package com.baec23.ludwig

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.baec23.ludwig.ui.ShaderTestScreen
import com.baec23.ludwig.ui.theme.LudwigTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LudwigTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    AnimatedTextTestScreen()
//                    TestScreen()
//                    PathExplorerScreen()
//                    PathStudyScreen()
                    ShaderTestScreen()
                }
            }
        }
    }
}
