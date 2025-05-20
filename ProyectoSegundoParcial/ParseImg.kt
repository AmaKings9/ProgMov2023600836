package com.example.tiendalibros

// ImageUtils.kt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ParseImg {
    // Convert drawable resource to Base64 String with thumbnail resizing
    fun convertDrawableToBase64(context: Context, drawableResId: Int): String {
        // Load the drawable as Bitmap
        val originalBitmap = BitmapFactory.decodeResource(context.resources, drawableResId)

        // Resize to thumbnail (150x150 max dimensions)
        val thumbnailBitmap = resizeToThumbnail(originalBitmap, 150, 150)

        // Convert resized Bitmap to ByteArray
        val outputStream = ByteArrayOutputStream()
        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        // Convert ByteArray to Base64 String
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Helper function to resize image to thumbnail
    private fun resizeToThumbnail(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Calculate the aspect ratio
        val aspectRatio = width.toFloat() / height.toFloat()

        // Calculate new dimensions maintaining aspect ratio
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        // Create scaled bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    // Convert Base64 String back to Bitmap (for display purposes)
    fun convertBase64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}