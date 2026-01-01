package com.example.jbchhymnbook.settings.domain.model

import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart
import com.example.jbchhymnbook.settings.data.getSystemLanguage

data class AppSettings(
    val visibleParts: Set<VoicePart> = VoicePart.entries.toSet(),
    val visibleLanguages: Set<Language> = Language.entries.toSet(),
    val appLanguage: Language = getSystemLanguage() // Default to system language
)

