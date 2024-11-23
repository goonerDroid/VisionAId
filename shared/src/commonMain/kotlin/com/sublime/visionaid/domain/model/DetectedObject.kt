package com.sublime.visionaid.domain.model

data class DetectedObject(
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox,
)
