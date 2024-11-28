package com.sublime.visionaid.android.services

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.sublime.visionaid.services.ImageFileProvider

class AndroidImageFileProvider(
    private val context: Context,
) : ImageFileProvider {
    override suspend fun getImageBytes(imagePath: String): ByteArray {
        val uri = Uri.parse(imagePath)
        return context.contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw IllegalStateException("Could not read image bytes")
    }

    override suspend fun getImageMimeType(imagePath: String): String {
        val uri = Uri.parse(imagePath)
        return context.contentResolver.getType(uri)
            ?: "image/jpeg"
    }

    override suspend fun getImageFileName(imagePath: String): String {
        val uri = Uri.parse(imagePath)
        val extension =
            MimeTypeMap
                .getSingleton()
                .getExtensionFromMimeType(getImageMimeType(imagePath)) ?: "jpg"
        return "image_${System.currentTimeMillis()}.$extension"
    }
}
