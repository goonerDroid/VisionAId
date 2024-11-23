package com.sublime.visionaid.domain.model

import kotlinx.datetime.Clock

data class ImageAnalysis(
    val id: String,
    val extractedText: String,
    val sceneDescription: String,
    val detectedObjects: List<DetectedObject> = emptyList(),
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
