package com.sublime.visionaid.android.ui

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun CameraContent(
    isLoading: Boolean,
    onCaptureImage: (Uri) -> Unit,
    onCaptureError: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val items =
        remember {
            listOf(
                ActionItem("Read Text"),
                ActionItem("Describe Scene"),
                ActionItem("Detect Objects"),
                ActionItem("Translate"),
                ActionItem("Custom"),
            )
        }

    // Set initial page to middle action item
    val initialPage = items.size / 2
    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { items.size },
        )

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            update = { previewView ->
                val cameraSelector =
                    CameraSelector
                        .Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview =
                            Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                        imageCapture =
                            ImageCapture
                                .Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                .build()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                        )
                    } catch (e: Exception) {
                        onCaptureError(e.message ?: "Camera setup failed")
                    }
                }, ContextCompat.getMainExecutor(context))
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                    pageSpacing = 32.dp,
                    contentPadding = PaddingValues(horizontal = 140.dp),
                    pageSize = PageSize.Fixed(72.dp),
                    beyondViewportPageCount = 2,
                ) { page ->
                    Box(
                        modifier =
                            Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(items[page].color)
                                .border(
                                    width = if (pagerState.currentPage == page) 2.dp else 0.dp,
                                    color = if (pagerState.currentPage == page) Color.White else Color.Transparent,
                                    shape = CircleShape,
                                ).clickable(enabled = !isLoading && pagerState.currentPage == page) {
                                    if (pagerState.currentPage == page) {
                                        coroutineScope.launch {
                                            takePhoto(
                                                context = context,
                                                imageCapture = imageCapture,
                                                onCaptureImage = onCaptureImage,
                                                onCaptureError = onCaptureError,
                                            )
                                        }
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (pagerState.currentPage == page) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Capture ${items[page].name}",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isLoading) "Analyzing image..." else items[pagerState.currentPage].name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onCaptureImage: (Uri) -> Unit,
    onCaptureError: (String) -> Unit,
) {
    val imageFile = createTempImageFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
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
                onCaptureError(exception.message ?: "An error occurred while capturing the image")
            }
        },
    )
}

private fun createTempImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.cacheDir
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}
