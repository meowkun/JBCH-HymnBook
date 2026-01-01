package com.example.jbchhymnbook.settings.data

import android.content.res.Configuration
import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.data.datasource.AndroidContextProvider

actual fun getSystemLanguage(): Language {
    val context = AndroidContextProvider.context
    if (context != null) {
        val config: Configuration = context.resources.configuration
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.locales[0] // Get first locale (API 24+)
        } else {
            @Suppress("DEPRECATION")
            config.locale // Fallback for older APIs
        }
        val languageCode = locale.language
        return mapLanguageCodeToLanguage(languageCode)
    }
    // Fallback if context not available
    return Language.ENGLISH
}

