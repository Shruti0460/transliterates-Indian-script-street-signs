package com.example.street_light.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.street_light.data.model.TransliterationResult
import com.example.street_light.data.repository.TransliterationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for history screen
 */
class HistoryViewModel(
    application: Application,
    private val repository: TransliterationRepository
) : AndroidViewModel(application) {

    val historyItems: StateFlow<List<TransliterationResult>> = 
        repository.getHistory()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteFromHistory(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

