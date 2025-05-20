package com.example.tiendalibros

import android.content.Context
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class PreferenceHelper(context: Context) {
    private val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val TAG = "PreferenceHelper"

    fun saveCardColor(color: Color) {
        val colorValue = color.toArgb()
        prefs.edit().putInt("card_color", colorValue).apply()
        Log.d(TAG, "Saved card_color: $colorValue (HEX: ${color.toHex()})")
    }

    fun getCardColor(): Color {
        val colorValue = prefs.getInt("card_color", Color(0xFFF5F5DC).toArgb())
        val color = Color(colorValue)
        Log.d(TAG, "Loaded card_color: $colorValue (HEX: ${color.toHex()})")
        return color
    }

    fun saveAppBackgroundColor(color: Color) {
        val colorValue = color.value.toInt()
        prefs.edit().putInt("app_background_color", colorValue).apply()
        Log.d(TAG, "Saved app_background_color: $colorValue (HEX: ${color.toHex()})")
    }

    fun getAppBackgroundColor(): Color {
        val colorValue = prefs.getInt("app_background_color", Color.White.value.toInt())
        val color = Color(colorValue)
        Log.d(TAG, "Loaded app_background_color: $colorValue (HEX: ${color.toHex()})")
        return color
    }
}