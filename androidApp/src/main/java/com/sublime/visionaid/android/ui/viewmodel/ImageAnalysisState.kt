package com.sublime.visionaid.android.ui.viewmodel

sealed class ImageAnalysisState {
    data object Idle : ImageAnalysisState()

    data object Loading : ImageAnalysisState()

    data class Success(
        val result: String,
    ) : ImageAnalysisState()

    data class Error(
        val message: String,
    ) : ImageAnalysisState()
}
