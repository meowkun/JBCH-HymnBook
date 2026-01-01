package com.example.jbchhymnbook.settings.presentation

import androidx.lifecycle.viewModelScope
import com.example.jbchhymnbook.core.presentation.MviViewModel
import com.example.jbchhymnbook.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : MviViewModel<SettingsState, SettingsIntent>(
    SettingsState()
) {
    
    init {
        onIntent(SettingsIntent.LoadSettings)
    }
    
    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadSettings -> loadSettings()
            is SettingsIntent.UpdateParts -> updateParts(intent.parts)
            is SettingsIntent.UpdateLanguages -> updateLanguages(intent.languages)
            is SettingsIntent.UpdateAppLanguage -> updateAppLanguage(intent.language)
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            settingsRepository.getSettings()
                .onSuccess { settings ->
                    updateState { copy(isLoading = false, settings = settings) }
                }
                .onFailure { error ->
                    updateState { 
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load settings"
                        ) 
                    }
                }
        }
    }
    
    private fun updateParts(parts: Set<com.example.jbchhymnbook.hymn.domain.model.VoicePart>) {
        val newSettings = state.value.settings.copy(visibleParts = parts)
        updateState { copy(settings = newSettings) }
        saveSettings(newSettings)
    }
    
    private fun updateLanguages(languages: Set<com.example.jbchhymnbook.hymn.domain.model.Language>) {
        val newSettings = state.value.settings.copy(visibleLanguages = languages)
        updateState { copy(settings = newSettings) }
        saveSettings(newSettings)
    }
    
    private fun updateAppLanguage(language: com.example.jbchhymnbook.hymn.domain.model.Language) {
        val newSettings = state.value.settings.copy(appLanguage = language)
        updateState { copy(settings = newSettings) }
        saveSettings(newSettings)
    }
    
    private fun saveSettings(settings: com.example.jbchhymnbook.settings.domain.model.AppSettings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
        }
    }
}

