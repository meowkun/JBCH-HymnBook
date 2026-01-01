package com.example.jbchhymnbook.hymn.presentation.display

import com.example.jbchhymnbook.hymn.domain.model.Hymn
import com.example.jbchhymnbook.settings.domain.model.AppSettings

data class HymnDisplayState(
    val isLoading: Boolean = true,
    val hymn: Hymn? = null,
    val filteredMusicXml: String = "",
    val settings: AppSettings = AppSettings(),
    val showSettingsSheet: Boolean = false,
    val error: String? = null
)

