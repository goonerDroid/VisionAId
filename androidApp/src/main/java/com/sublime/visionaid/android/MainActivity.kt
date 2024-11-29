package com.sublime.visionaid.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sublime.visionaid.android.services.AndroidImageFileProvider
import com.sublime.visionaid.android.ui.MainScreen
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
                MainScreen(imageAnalysisService)
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
