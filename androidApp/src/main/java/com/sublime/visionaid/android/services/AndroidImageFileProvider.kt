package com.sublime.visionaid.android.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import com.sublime.visionaid.services.ImageFileProvider
import java.io.ByteArrayOutputStream

class AndroidImageFileProvider(
    private val context: Context,
) : ImageFileProvider {
    companion object {
        private const val COMPRESSION_QUALITY =
            80 // Adjustable compression quality (0-100) ~80 is good value maintaining the image fidelity and size constraints
        private const val MAX_IMAGE_DIMENSION = 1024 // Maximum dimension for either width or height
    }

    override suspend fun getImageBytes(imagePath: String): ByteArray {
        val uri = Uri.parse(imagePath)
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // Decode image dimensions first
            val options =
                BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
            BitmapFactory.decodeStream(inputStream, null, options)

            // Calculate inSampleSize
            val inSampleSize =
                calculateInSampleSize(options)

            // Reset stream and decode with compression
            context.contentResolver.openInputStream(uri)?.use { resetStream ->
                val compressOptions =
                    BitmapFactory.Options().apply {
                        this.inSampleSize = inSampleSize
                    }
                val bitmap = BitmapFactory.decodeStream(resetStream, null, compressOptions)

                // Compress to bytes
                ByteArrayOutputStream().use { outputStream ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
                    bitmap?.recycle()
                    outputStream.toByteArray()
                }
            } ?: throw IllegalStateException("Could not read image bytes")
        } ?: throw IllegalStateException("Could not read image bytes")
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > MAX_IMAGE_DIMENSION || width > MAX_IMAGE_DIMENSION) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= MAX_IMAGE_DIMENSION && halfWidth / inSampleSize >= MAX_IMAGE_DIMENSION) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    override suspend fun getImageMimeType(imagePath: String): String {
        val uri = Uri.parse(imagePath)
        return context.contentResolver.getType(uri) ?: "image/jpeg"
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
