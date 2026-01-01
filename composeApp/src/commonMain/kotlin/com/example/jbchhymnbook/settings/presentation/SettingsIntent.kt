package com.example.jbchhymnbook.settings.presentation

import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart

sealed interface SettingsIntent {
    data object LoadSettings : SettingsIntent
    data class UpdateParts(val parts: Set<VoicePart>) : SettingsIntent
    data class UpdateLanguages(val languages: Set<Language>) : SettingsIntent
    data class UpdateAppLanguage(val language: Language) : SettingsIntent
}

