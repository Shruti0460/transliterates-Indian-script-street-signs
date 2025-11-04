package com.example.street_light.data.repository

import com.example.street_light.data.database.HistoryDao
import com.example.street_light.data.database.HistoryItem
import com.example.street_light.data.model.IndianScript
import com.example.street_light.data.model.TransliterationResult
import com.example.street_light.domain.transliteration.TransliterationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing transliteration operations and history
 */
class TransliterationRepository(
    private val transliterationEngine: TransliterationEngine,
    private val historyDao: HistoryDao
) {
    fun transliterate(
        text: String,
        targetScript: IndianScript,
        detectedScript: IndianScript? = null
    ): TransliterationResult {
        val detected = detectedScript ?: IndianScript.detectScript(text)
        val transliterated = transliterationEngine.transliterate(text, targetScript, detected)
        
        return TransliterationResult(
            originalText = text,
            detectedScript = detected,
            transliteratedText = transliterated,
            targetScript = targetScript
        )
    }

    fun getHistory(): Flow<List<TransliterationResult>> {
        return historyDao.getRecentHistory().map { items ->
            items.map { item ->
                TransliterationResult(
                    originalText = item.originalText,
                    detectedScript = item.detectedScriptEnum(),
                    transliteratedText = item.transliteratedText,
                    targetScript = item.toScript() ?: IndianScript.DEVANAGARI,
                    timestamp = item.timestamp,
                    imageUri = item.imageUri
                )
            }
        }
    }

    suspend fun saveToHistory(result: TransliterationResult) {
        val item = HistoryItem(
            originalText = result.originalText,
            detectedScript = result.detectedScript?.isoCode,
            transliteratedText = result.transliteratedText,
            targetScript = result.targetScript.isoCode,
            timestamp = result.timestamp,
            imageUri = result.imageUri
        )
        historyDao.insertHistory(item)
    }

    suspend fun deleteFromHistory(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAllHistory()
    }
}

