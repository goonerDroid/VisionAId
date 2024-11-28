package com.sublime.visionaid.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.sublime.visionaid.android.services.AndroidImageFileProvider
import com.sublime.visionaid.android.ui.CameraScreen
import com.sublime.visionaid.services.ImageAnalysisService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private lateinit var imageAnalysisService: ImageAnalysisService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize HTTP client
        val httpClient =
            HttpClient(Android) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            prettyPrint = true
                        },
                    )
                }
            }

        // Initialize the image analysis service with Android-specific file provider
        imageAnalysisService =
            ImageAnalysisService(
                client = httpClient,
                fileProvider = AndroidImageFileProvider(this),
            )

        setContent {
            val coroutineScope = rememberCoroutineScope()

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CameraScreen(
                        onCaptureImage = { uri ->
                            coroutineScope.launch {
                                try {
                                    val result = imageAnalysisService.analyzeImage(uri.toString())
                                    result
                                        .onSuccess { response ->
                                            Log.d("ImageAnalysis", "Caption: ${response.caption}")
                                            Log.d("ImageAnalysis", "Tags: ${response.tags}")
                                            Log.d("ImageAnalysis", "Objects: ${response.objects}")
                                        }.onFailure { error ->
                                            Log.e("ImageAnalysis", "Error analyzing image", error)
                                        }
                                } catch (e: Exception) {
                                    Log.e("ImageAnalysis", "Error", e)
                                }
                            }
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
