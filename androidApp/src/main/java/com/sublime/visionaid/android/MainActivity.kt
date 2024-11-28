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
                            /* // Convert URI to Bitmap
                             contentResolver.openInputStream(uri)?.use { inputStream ->
                                 val bitmap = BitmapFactory.decodeStream(inputStream)

                                 // Process image with Azure Vision
                                 azureVisionClient
                                     .processImage(bitmap)
                                     .onEach { result ->
                                         when (result) {
                                             is AzureVisionClient.VisionResult.Success -> {
                                                 Log.d("Vision", "Text: ${result.text}")
                                                 Log.d("Vision", "Confidence: ${result.confidence}")
                                                 // Here you can implement TTS to read the text
                                             }

                                             is AzureVisionClient.VisionResult.Error -> {
                                                 Log.e("Vision", result.message)
                                             }
                                         }
                                     }.catch { e ->
                                         Log.e("Vision", "Error processing image", e)
                                     }
                             }*/

//                            val key = azureVisionClient.getKey()
                        },
                        onCaptureError = { error ->
                            Log.e("Camera", error)
                        },
                    )
                }
            }
        }
    }
}
