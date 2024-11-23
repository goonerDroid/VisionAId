package com.sublime.visionaid.repository

import com.sublime.visionaid.domain.model.ImageAnalysis

interface ImageAnalysisRepository {
    fun analyzeImage(imageData: ByteArray): ImageAnalysis

    fun extractText(imageData: ByteArray): String

    fun describeScene(imageData: ByteArray): String
}
