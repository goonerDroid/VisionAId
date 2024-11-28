package com.sublime.visionaid.services

interface ImageFileProvider {
    suspend fun getImageBytes(imagePath: String): ByteArray

    suspend fun getImageMimeType(imagePath: String): String

    suspend fun getImageFileName(imagePath: String): String
}
