package com.example.jbchhymnbook.settings.data

import com.example.jbchhymnbook.hymn.domain.model.Language

/**
 * Detects the system language and maps it to our Language enum
 */
expect fun getSystemLanguage(): Language

/**
 * Maps a language code (e.g., "ko", "en", "zh", "es") to Language enum
 */
fun mapLanguageCodeToLanguage(languageCode: String): Language {
    val code = languageCode.lowercase().substringBefore("-") // Handle "ko-KR" -> "ko"
    return when (code) {
        "ko" -> Language.KOREAN
        "en" -> Language.ENGLISH
        "zh" -> Language.CHINESE
        "es" -> Language.SPANISH
        else -> Language.ENGLISH // Default fallback
    }
}

