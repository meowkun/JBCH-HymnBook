package com.example.jbchhymnbook.hymn.presentation.display

import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart

sealed interface HymnDisplayIntent {
    data class LoadHymn(val hymnId: String) : HymnDisplayIntent
    data object ToggleSettingsSheet : HymnDisplayIntent
    data class UpdateParts(val parts: Set<VoicePart>) : HymnDisplayIntent
    data class UpdateLanguages(val languages: Set<Language>) : HymnDisplayIntent
    data class UpdateAppLanguage(val language: Language) : HymnDisplayIntent
}

