package com.example.street_light

import android.app.Application
import androidx.room.Room
import com.example.street_light.data.database.AppDatabase
import com.example.street_light.data.ocr.OCRService
import com.example.street_light.data.preferences.AppPreferences
import com.example.street_light.data.repository.TransliterationRepository
import com.example.street_light.domain.transliteration.TransliterationEngine

/**
 * Application class for dependency management
 */
class StreetLightApplication : Application() {
    
    // Database
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "streetlight_database"
        ).build()
    }

    // Preferences
    val preferences: AppPreferences by lazy {
        AppPreferences(applicationContext)
    }

    // Services
    val ocrService: OCRService by lazy {
        OCRService()
    }

    // Domain
    val transliterationEngine: TransliterationEngine by lazy {
        TransliterationEngine()
    }

    // Repository
    val transliterationRepository: TransliterationRepository by lazy {
        TransliterationRepository(
            transliterationEngine = transliterationEngine,
            historyDao = database.historyDao()
        )
    }
}

