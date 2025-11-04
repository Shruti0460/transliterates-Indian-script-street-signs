package com.example.street_light.data.model

/**
 * Enum representing all supported Indian scripts for transliteration
 */
enum class IndianScript(
    val displayName: String,
    val nativeName: String,
    val isoCode: String,
    val languages: List<String>
) {
    DEVANAGARI(
        displayName = "Devanagari",
        nativeName = "देवनागरी",
        isoCode = "Deva",
        languages = listOf("Hindi", "Marathi", "Sanskrit", "Nepali")
    ),
    TAMIL(
        displayName = "Tamil",
        nativeName = "தமிழ்",
        isoCode = "Taml",
        languages = listOf("Tamil")
    ),
    BENGALI(
        displayName = "Bengali",
        nativeName = "বাংলা",
        isoCode = "Beng",
        languages = listOf("Bengali", "Assamese")
    ),
    TELUGU(
        displayName = "Telugu",
        nativeName = "తెలుగు",
        isoCode = "Telu",
        languages = listOf("Telugu")
    ),
    KANNADA(
        displayName = "Kannada",
        nativeName = "ಕನ್ನಡ",
        isoCode = "Knda",
        languages = listOf("Kannada")
    ),
    MALAYALAM(
        displayName = "Malayalam",
        nativeName = "മലയാളം",
        isoCode = "Mlym",
        languages = listOf("Malayalam")
    ),
    GURMUKHI(
        displayName = "Gurmukhi",
        nativeName = "ਗੁਰਮੁਖੀ",
        isoCode = "Guru",
        languages = listOf("Punjabi")
    ),
    GUJARATI(
        displayName = "Gujarati",
        nativeName = "ગુજરાતી",
        isoCode = "Gujr",
        languages = listOf("Gujarati")
    );

    companion object {
        fun fromIsoCode(isoCode: String): IndianScript? {
            return values().find { it.isoCode == isoCode }
        }

        fun detectScript(text: String): IndianScript? {
            // Simple character range detection
            return when {
                text.any { it in '\u0900'..'\u097F' } -> DEVANAGARI
                text.any { it in '\u0B80'..'\u0BFF' } -> TAMIL
                text.any { it in '\u0980'..'\u09FF' } -> BENGALI
                text.any { it in '\u0C00'..'\u0C7F' } -> TELUGU
                text.any { it in '\u0C80'..'\u0CFF' } -> KANNADA
                text.any { it in '\u0D00'..'\u0D7F' } -> MALAYALAM
                text.any { it in '\u0A00'..'\u0A7F' } -> GURMUKHI
                text.any { it in '\u0A80'..'\u0AFF' } -> GUJARATI
                else -> null
            }
        }
    }
}

