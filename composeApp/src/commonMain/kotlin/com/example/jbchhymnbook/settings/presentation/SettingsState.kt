package com.example.jbchhymnbook.settings.presentation

import com.example.jbchhymnbook.settings.domain.model.AppSettings

data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = false,
    val error: String? = null
)

