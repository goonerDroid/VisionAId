package com.sublime.visionaid.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sublime.visionaid.android.ui.CameraScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CameraScreen(
                        onCaptureImage = { uri ->
                            // You can now pass this to your Azure Vision services
                            Log.e("MainActivity", uri.toString())
                        },
                        onCaptureError = { error ->
                            // Handle any errors that occur during camera operation
                            Log.e("MainActivity", error)
                        },
                    )
                }
            }
        }
    }
}
