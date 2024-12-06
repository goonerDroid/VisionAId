package com.sublime.visionaid.services

import com.sublime.visionaid.models.ImageAnalysisResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class ImageAnalysisService(
    private val client: HttpClient,
    private val fileProvider: ImageFileProvider,
    private val baseUrl: String = BuildConfig.BASE_URL
) {
    suspend fun analyzeImage(imagePath: String): Result<ImageAnalysisResponse> =
        try {
            val imageBytes = fileProvider.getImageBytes(imagePath)
            val fileName = fileProvider.getImageFileName(imagePath)
            val mimeType = fileProvider.getImageMimeType(imagePath)

            val response =
                client.post("$baseUrl/analyze-image") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append(
                                    "file",
                                    imageBytes,
                                    Headers.build {
                                        append(HttpHeaders.ContentType, mimeType)
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=$fileName",
                                        )
                                    },
                                )
                            },
                        ),
                    )
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val analysisResponse = response.body<ImageAnalysisResponse>()
                    if (analysisResponse.hasError) {
                        Result.failure(Exception(analysisResponse.error))
                    } else {
                        Result.success(analysisResponse)
                    }
                }

                else -> {
                    val errorBody = response.bodyAsText()
                    Result.failure(Exception("API Error: ${response.status}. $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
