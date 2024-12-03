package com.sublime.visionaid.android.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sublime.visionaid.android.ui.viewmodel.CameraViewModel
import com.sublime.visionaid.android.ui.viewmodel.ImageAnalysisState

@Suppress("ktlint:standard:function-naming")
@Composable
fun MainScreen(viewModel: CameraViewModel) {
    val navController = rememberNavController()
    val analysisState by viewModel.analysisState.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "camera") {
            composable("camera") {
                CameraScreen(
                    isLoading = analysisState is ImageAnalysisState.Loading,
                    onCaptureImage = { uri, actionType ->
                        imageUri = uri
                        viewModel.analyzeImage(uri.toString(), actionType)
                    },
                    onCaptureError = { error ->
                        // Handle error
                    },
                    cameraViewModel = viewModel,
                )
                LaunchedEffect(analysisState) {
                    if (analysisState is ImageAnalysisState.Success && imageUri != null) {
                        navController.navigate("results") {
                            launchSingleTop = true
                        }
                    }
                }
            }

            composable("results") {
                val handleBack: () -> Unit = {
                    imageUri = null
                    viewModel.resetState()
                    navController.popBackStack()
                }

                BackHandler(onBack = handleBack)

                imageUri?.let { uri ->
                    AnalysisResultScreen(
                        imageUri = uri,
                        analysisState = analysisState,
                        onBack = handleBack,
                    )
                }
            }
        }
    }
}
