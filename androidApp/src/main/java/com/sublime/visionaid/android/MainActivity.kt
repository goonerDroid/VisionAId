package com.sublime.visionaid.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sublime.visionaid.android.services.AndroidImageFileProvider
import com.sublime.visionaid.android.ui.helpers.MyApplicationTheme
import com.sublime.visionaid.android.ui.screens.MainScreen
import com.sublime.visionaid.android.ui.viewmodel.CameraViewModel
import com.sublime.visionaid.services.ImageAnalysisService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private lateinit var imageAnalysisService: ImageAnalysisService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeServices()

        setContent {
            MyApplicationTheme {
                val viewModel = viewModel { CameraViewModel(imageAnalysisService) }
                MainScreen(viewModel)
            }
        }
    }

    private fun initializeServices() {
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
