package com.example.street_light.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Result of OCR and transliteration process
 */
@Parcelize
data class TransliterationResult(
    val originalText: String,
    val detectedScript: IndianScript?,
    val transliteratedText: String,
    val targetScript: IndianScript,
    val confidence: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null
) : Parcelable

