package com.sublime.visionaid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetectedObject(
    val name: String,
    val confidence: Double,
    @SerialName("bounding_box")
    val boundingBox: BoundingBox,
)
