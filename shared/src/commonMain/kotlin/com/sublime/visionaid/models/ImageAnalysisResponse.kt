package com.sublime.visionaid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageAnalysisResponse(
    val caption: String? = null,
    @SerialName("text_content")
    val textContent: String? = null,
    val tags: List<Tag> = emptyList(),
    val objects: List<DetectedObject> = emptyList(),
    val error: String? = null,
) {
    // Helper properties to safely access nullable fields
    val hasCaption: Boolean
        get() = !caption.isNullOrBlank()

    val hasTextContent: Boolean
        get() = !textContent.isNullOrBlank()

    val hasTags: Boolean
        get() = tags.isNotEmpty()

    val hasObjects: Boolean
        get() = objects.isNotEmpty()

    val hasError: Boolean
        get() = !error.isNullOrBlank()
}
