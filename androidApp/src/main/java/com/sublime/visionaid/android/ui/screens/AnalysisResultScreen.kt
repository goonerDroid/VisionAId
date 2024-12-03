@file:OptIn(ExperimentalMaterial3Api::class)

package com.sublime.visionaid.android.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sublime.visionaid.android.ui.viewmodel.ImageAnalysisState

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun AnalysisResultScreen(
    imageUri: Uri,
    analysisState: ImageAnalysisState,
    onBack: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(analysisState) {
        if (analysisState is ImageAnalysisState.Error) {
            snackBarHostState.showSnackbar(
                message = analysisState.message,
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    snackbarData = data,
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Image Scene") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.45f),
                        titleContentColor = Color.White,
                    ),
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Analyzed Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            if (analysisState is ImageAnalysisState.Success) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(16.dp),
                ) {
                    Text(
                        text = analysisState.result,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontSize = 28.sp,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                    )
                }
            }
        }
    }
}
