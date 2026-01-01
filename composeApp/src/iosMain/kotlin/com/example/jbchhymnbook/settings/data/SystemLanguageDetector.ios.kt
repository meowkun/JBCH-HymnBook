package com.example.jbchhymnbook.settings.data

import com.example.jbchhymnbook.hymn.domain.model.Language
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual fun getSystemLanguage(): Language {
    val preferredLanguages = NSLocale.preferredLanguages
    if (preferredLanguages.isNotEmpty()) {
        val languageCode = preferredLanguages[0] as? String ?: ""
        return mapLanguageCodeToLanguage(languageCode)
    }
    // Fallback if no preferred language
    return Language.ENGLISH
}

