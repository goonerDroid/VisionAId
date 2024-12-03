package com.sublime.visionaid.android.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.visionaid.android.ui.helpers.ActionType
import com.sublime.visionaid.services.ImageAnalysisService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val imageAnalysisService: ImageAnalysisService,
) : ViewModel() {
    private var _imageCapture = MutableStateFlow<ImageCapture?>(null)
    val imageCapture: StateFlow<ImageCapture?> = _imageCapture.asStateFlow()

    private var _analysisState = MutableStateFlow<ImageAnalysisState>(ImageAnalysisState.Idle)
    val analysisState: StateFlow<ImageAnalysisState> = _analysisState.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null

    fun analyzeImage(
        imagePath: String,
        actionType: ActionType,
    ) {
        viewModelScope.launch {
            try {
                _analysisState.value = ImageAnalysisState.Loading
                val result =
                    when (actionType) {
                        ActionType.DESCRIBE_SCENE -> imageAnalysisService.analyzeImage(imagePath)
                        ActionType.READ_TEXT -> imageAnalysisService.analyzeImage(imagePath)
                        ActionType.DETECT_OBJECTS -> imageAnalysisService.analyzeImage(imagePath)
                        ActionType.TRANSLATE -> imageAnalysisService.analyzeImage(imagePath)
                        ActionType.CUSTOM -> imageAnalysisService.analyzeImage(imagePath)
                    }

                result
                    .onSuccess { response ->
                        _analysisState.value =
                            ImageAnalysisState.Success(
                                when (actionType) {
                                    ActionType.DESCRIBE_SCENE ->
                                        response.caption
                                            ?: "No description available"

                                    ActionType.READ_TEXT -> response.textContent ?: "No text found"
                                    ActionType.DETECT_OBJECTS ->
                                        response.textContent
                                            ?: "No text found"

                                    ActionType.TRANSLATE -> response.textContent ?: "No text found"
                                    ActionType.CUSTOM -> response.caption ?: "No analysis available"
                                },
                            )
                    }.onFailure { error ->
                        _analysisState.value =
                            ImageAnalysisState.Error("Error analyzing image: ${error.message}")
                    }
            } catch (e: Exception) {
                _analysisState.value = ImageAnalysisState.Error("Error: ${e.message}")
            }
        }
    }

    public override fun onCleared() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        _imageCapture.value = null
    }

    @SuppressLint("RestrictedApi")
    fun setupCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onError: (String) -> Unit,
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                val preview =
                    Preview
                        .Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                _imageCapture.value =
                    ImageCapture
                        .Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .build()

                val cameraSelector =
                    CameraSelector
                        .Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    _imageCapture.value,
                )
            } catch (e: Exception) {
                onError(e.message ?: "Camera setup failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
