package com.sublime.visionaid.android

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sublime.visionaid.android.services.AndroidImageFileProvider
import com.sublime.visionaid.android.ui.AnalysisResultsScreen
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

        // Initialize services
        initializeServices()

        setContent {
            MyApplicationTheme {
                MainScreen(imageAnalysisService)
            }
        }
    }

    private fun initializeServices() {
        // Initialize your HTTP client and other services as shown in your existing code
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

        imageAnalysisService =
            ImageAnalysisService(
                client = httpClient,
                fileProvider = AndroidImageFileProvider(this),
            )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MainScreen(imageAnalysisService: ImageAnalysisService) {
    Surface(color = MaterialTheme.colorScheme.background) {
        val navController = rememberNavController()
        var currentImageUri by remember { mutableStateOf<Uri?>(null) }
        var analysisResult by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()

        NavHost(navController = navController, startDestination = "camera") {
            composable("camera") {
                CameraScreen(
                    onCaptureImage = { uri ->
                        currentImageUri = uri
                        // Analyze image and navigate to results
                        coroutineScope.launch {
                            try {
                                val result = imageAnalysisService.analyzeImage(uri.toString())
                                result
                                    .onSuccess { response ->
                                        analysisResult =
                                            response.getMostConfidentDenseCaption()?.text
                                                ?: "No description available"
                                        navController.navigate("results")
                                    }.onFailure { error ->
                                        // Handle error
                                        analysisResult = "Error analyzing image: ${error.message}"
                                    }
                            } catch (e: Exception) {
                                // Handle exception
                                analysisResult = "Error: ${e.message}"
                            }
                        }
                    },
                    onCaptureError = { error ->
                        // Handle error
                        analysisResult = "Camera error: $error"
                    },
                )
            }

            composable("results") {
                currentImageUri?.let { uri ->
                    analysisResult?.let { result ->
                        AnalysisResultsScreen(
                            imageUri = uri,
                            analysisResult = result,
                            onBack = {
                                navController.navigateUp()
                            },
                        )
                    }
                }
            }
        }
    }
}
