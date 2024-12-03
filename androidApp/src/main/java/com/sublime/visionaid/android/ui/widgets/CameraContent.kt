package com.sublime.visionaid.android.ui.widgets

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sublime.visionaid.android.ui.helpers.ActionItem
import com.sublime.visionaid.android.ui.helpers.ActionType
import com.sublime.visionaid.android.ui.viewmodel.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun CameraContent(
    viewModel: CameraViewModel,
    isLoading: Boolean,
    onCaptureImage: (Uri, ActionType) -> Unit,
    onCaptureError: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val items =
        remember {
            listOf(
                ActionItem("Read Text", ActionType.READ_TEXT),
                ActionItem("Describe Scene", ActionType.DESCRIBE_SCENE),
                ActionItem("Detect Objects", ActionType.DETECT_OBJECTS),
                ActionItem("Translate", ActionType.TRANSLATE),
                ActionItem("Custom", ActionType.CUSTOM),
            )
        }

    val previewView =
        remember {
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        }

    val pagerState =
        rememberPagerState(
            initialPage = items.size / 2,
            pageCount = { items.size },
        )

    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.onCleared()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                viewModel.setupCamera(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    previewView = view,
                    onError = onCaptureError,
                )
            },
        )

        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(vertical = 24.dp),
        ) {
            ActionItemsPager(
                items = items,
                pagerState = pagerState,
                isLoading = isLoading,
                onCaptureImage = onCaptureImage,
                viewModel = viewModel,
                context = context,
                onCaptureError = onCaptureError,
            )
        }
    }
}

suspend fun takePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onCaptureImage: (Uri) -> Unit,
    onCaptureError: (String) -> Unit,
) = withContext(Dispatchers.IO) {
    try {
        val imageFile = createTempImageFile(context)
        val outputOptions =
            ImageCapture.OutputFileOptions
                .Builder(imageFile)
                .setMetadata(
                    ImageCapture.Metadata().apply {
                        isReversedHorizontal = false
                    },
                ).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { uri ->
                        onCaptureImage(uri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onCaptureError(exception.message ?: "Image capture failed")
                }
            },
        )
    } catch (e: Exception) {
        onCaptureError("Failed to create image file: ${e.message}")
    }
}

private fun createTempImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.cacheDir
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}
