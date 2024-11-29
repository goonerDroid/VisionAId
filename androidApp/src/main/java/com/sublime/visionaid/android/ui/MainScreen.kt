package com.sublime.visionaid.android.ui

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sublime.visionaid.services.ImageAnalysisService
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun MainScreen(imageAnalysisService: ImageAnalysisService) {
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    var analysisResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "camera") {
            composable("camera") {
                CameraScreen(
                    isLoading = isLoading,
                    onCaptureImage = { uri ->
                        currentImageUri = uri
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val result = imageAnalysisService.analyzeImage(uri.toString())
                                result
                                    .onSuccess { response ->
                                        analysisResult =
                                            response.caption ?: "No description available"
                                        navController.navigate("results")
                                    }.onFailure { error ->
                                        errorMessage = "Error analyzing image: ${error.message}"
                                    }
                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    onCaptureError = { error ->
                        errorMessage = "Camera error: $error"
                    },
                )
            }

            composable("results") {
                currentImageUri?.let { uri ->
                    analysisResult?.let { result ->
                        AnalysisResultsScreen(
                            imageUri = uri,
                            analysisResult = result,
                            onBack = { navController.navigateUp() },
                        )
                    }
                }
            }
        }
    }
}
