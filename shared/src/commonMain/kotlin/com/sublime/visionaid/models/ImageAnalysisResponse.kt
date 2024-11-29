package com.sublime.visionaid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageAnalysisResponse(
    val caption: String? = null,
    @SerialName("dense_captions")
    val denseCaptions: List<DenseCaption> = emptyList(),
    @SerialName("text_content")
    val textContent: String? = null,
    val error: String? = null,
) {
    val hasCaption: Boolean
        get() = !caption.isNullOrBlank()

    val hasDenseCaptions: Boolean
        get() = denseCaptions.isNotEmpty()

    val hasTextContent: Boolean
        get() = !textContent.isNullOrBlank()

    val hasError: Boolean
        get() = !error.isNullOrBlank()

    fun getMostConfidentDenseCaption(): DenseCaption? = denseCaptions.maxByOrNull { it.confidence }

    fun getDenseCaptionsText(): String = denseCaptions.joinToString("\n") { "${it.text} (Confidence: ${(it.confidence * 100).toInt()}%)" }
}
