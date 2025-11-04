package com.example.street_light.data.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

/**
 * Service for performing OCR on images using ML Kit
 */
class OCRService {
    
    private val latinRecognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())
    private val devanagariRecognizer = TextRecognition.getClient(
        DevanagariTextRecognizerOptions.Builder().build()
    )

    /**
     * Performs OCR on a bitmap image
     * @return extracted text or null if recognition fails
     */
    suspend fun recognizeText(bitmap: Bitmap): String? {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            
            // Try Devanagari first (most common), fallback to Latin
            val devanagariResult = devanagariRecognizer.process(image).await()
            if (devanagariResult.text.isNotBlank()) {
                return devanagariResult.text
            }
            
            // Fallback to Latin recognizer
            val latinResult = latinRecognizer.process(image).await()
            latinResult.text.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Performs OCR with script detection hint
     */
    suspend fun recognizeTextWithScript(
        bitmap: Bitmap,
        preferDevanagari: Boolean = false
    ): String? {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            
            val recognizer = if (preferDevanagari) devanagariRecognizer else latinRecognizer
            val result = recognizer.process(image).await()
            result.text.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close() {
        latinRecognizer.close()
        devanagariRecognizer.close()
    }
}

