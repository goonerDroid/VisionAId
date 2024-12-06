package com.sublime.visionaid.models

sealed class ImageAnalysisState {
    data object Idle : ImageAnalysisState()

    data object Loading : ImageAnalysisState()

    data class Success(
        val result: String,
        val detectedObjects: List<String> = emptyList(),
        val confidence: Double = 0.0,
        val language: String? = null
    ) : ImageAnalysisState()

    data class Error(
        val message: String,
        val errorCode: Int? = null,
        val isRetry: Boolean = false
    ) : ImageAnalysisState()

    fun isProcessing(): Boolean = this is Loading
    fun hasError(): Boolean = this is Error
    fun getResultOrNull(): String? = (this as? Success)?.result
}
