package com.example.street_light.ui.camera

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.street_light.data.model.IndianScript
import com.example.street_light.data.model.TransliterationResult
import com.example.street_light.data.ocr.OCRService
import com.example.street_light.data.preferences.AppPreferences
import com.example.street_light.data.repository.TransliterationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for camera screen
 */
class CameraViewModel(
    application: Application,
    private val ocrService: OCRService,
    private val transliterationRepository: TransliterationRepository,
    private val preferences: AppPreferences
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            targetScript = preferences.preferredScript
        )
    }

    fun captureAndProcess(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            try {
                // Perform OCR
                val extractedText = ocrService.recognizeText(bitmap)
                
                if (extractedText.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "No text detected in image"
                    )
                    return@launch
                }

                // Perform transliteration
                val result = transliterationRepository.transliterate(
                    text = extractedText,
                    targetScript = _uiState.value.targetScript
                )

                // Save to history
                transliterationRepository.saveToHistory(result)

                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    lastResult = result,
                    showResult = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    fun setTargetScript(script: IndianScript) {
        _uiState.value = _uiState.value.copy(targetScript = script)
        preferences.preferredScript = script
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(showResult = false, lastResult = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    override fun onCleared() {
        super.onCleared()
        ocrService.close()
    }
}

data class CameraUiState(
    val isProcessing: Boolean = false,
    val targetScript: IndianScript = IndianScript.DEVANAGARI,
    val lastResult: TransliterationResult? = null,
    val showResult: Boolean = false,
    val error: String? = null
)

