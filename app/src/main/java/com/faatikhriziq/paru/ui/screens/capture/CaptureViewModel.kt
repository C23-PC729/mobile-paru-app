package com.faatikhriziq.paru.ui.screens.capture

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CaptureViewModel : ViewModel() {
    val imageBitmap = mutableStateOf<Bitmap?>(null)
    val aqiResult = mutableStateOf("")

    fun loadCapturedImage(savedUri: android.net.Uri, context: Context) {
        viewModelScope.launch {
            val bitmap = loadImageBitmap(savedUri, context)
            imageBitmap.value = bitmap
            val aqi = performAQIAnalysis(bitmap, context)
            aqiResult.value = "AQI: $aqi"
        }
    }
}