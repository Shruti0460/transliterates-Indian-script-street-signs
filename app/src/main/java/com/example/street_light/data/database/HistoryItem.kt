package com.example.street_light.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.street_light.data.model.IndianScript

/**
 * Room entity for storing transliteration history
 */
@Entity(tableName = "transliteration_history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val detectedScript: String?,
    val transliteratedText: String,
    val targetScript: String,
    val timestamp: Long,
    val imageUri: String? = null
) {
    fun toScript(): IndianScript? {
        return IndianScript.values().find { it.isoCode == targetScript }
    }

    fun detectedScriptEnum(): IndianScript? {
        return detectedScript?.let { IndianScript.values().find { script -> script.isoCode == it } }
    }
}

