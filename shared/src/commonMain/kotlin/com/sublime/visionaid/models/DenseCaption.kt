package com.sublime.visionaid.models

import kotlinx.serialization.Serializable

@Serializable
data class DenseCaption(
    val text: String,
    val confidence: Double,
)
