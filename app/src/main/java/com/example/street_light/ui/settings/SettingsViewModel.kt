package com.example.street_light.ui.settings

import android.app.Application
import com.example.street_light.data.model.IndianScript
import com.example.street_light.data.preferences.AppPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for settings screen
 */
class SettingsViewModel(
    application: Application,
    private val preferences: AppPreferences
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            preferredScript = preferences.preferredScript,
            fontSizeMultiplier = preferences.fontSizeMultiplier
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setPreferredScript(script: IndianScript) {
        preferences.preferredScript = script
        _uiState.value = _uiState.value.copy(preferredScript = script)
    }

    fun setFontSizeMultiplier(multiplier: Float) {
        preferences.fontSizeMultiplier = multiplier
        _uiState.value = _uiState.value.copy(fontSizeMultiplier = multiplier)
    }
}

data class SettingsUiState(
    val preferredScript: IndianScript = IndianScript.DEVANAGARI,
    val fontSizeMultiplier: Float = 1.0f
)

