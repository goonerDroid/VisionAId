package com.sublime.visionaid.models

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val name: String,
    val confidence: Double,
)
