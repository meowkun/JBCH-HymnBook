package com.example.jbchhymnbook.hymn.presentation.display

import androidx.lifecycle.viewModelScope
import com.example.jbchhymnbook.core.presentation.MviViewModel
import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart
import com.example.jbchhymnbook.hymn.domain.repository.HymnRepository
import com.example.jbchhymnbook.hymn.domain.usecase.FilterMusicXmlUseCase
import com.example.jbchhymnbook.hymn.domain.usecase.GetHymnDetailUseCase
import com.example.jbchhymnbook.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

class HymnDisplayViewModel(
    private val getHymnDetailUseCase: GetHymnDetailUseCase,
    private val hymnRepository: HymnRepository,
    private val filterMusicXmlUseCase: FilterMusicXmlUseCase,
    private val settingsRepository: SettingsRepository,
    private val hymnId: String
) : MviViewModel<HymnDisplayState, HymnDisplayIntent>(
    HymnDisplayState()
) {
    
    init {
        onIntent(HymnDisplayIntent.LoadHymn(hymnId))
        loadSettings()
    }
    
    override fun onIntent(intent: HymnDisplayIntent) {
        when (intent) {
            is HymnDisplayIntent.LoadHymn -> loadHymn(intent.hymnId)
            is HymnDisplayIntent.ToggleSettingsSheet -> {
                updateState { copy(showSettingsSheet = !showSettingsSheet) }
            }
            is HymnDisplayIntent.UpdateParts -> updateParts(intent.parts)
            is HymnDisplayIntent.UpdateLanguages -> updateLanguages(intent.languages)
            is HymnDisplayIntent.UpdateAppLanguage -> updateAppLanguage(intent.language)
        }
    }
    
    private fun loadHymn(hymnId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            getHymnDetailUseCase(hymnId)
                .onSuccess { hymn ->
                    hymnRepository.getMusicXml(hymnId)
                        .onSuccess { musicXml ->
                            val filteredXml = filterMusicXmlUseCase(
                                musicXml,
                                state.value.settings.visibleParts,
                                state.value.settings.visibleLanguages,
                                state.value.settings.appLanguage
                            )
                            updateState { 
                                copy(
                                    isLoading = false,
                                    hymn = hymn,
                                    filteredMusicXml = filteredXml
                                ) 
                            }
                        }
                        .onFailure { error ->
                            updateState { 
                                copy(
                                    isLoading = false,
                                    error = error.message ?: "Failed to load music XML"
                                ) 
                            }
                        }
                }
                .onFailure { error ->
                    updateState { 
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load hymn"
                        ) 
                    }
                }
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings()
                .onSuccess { settings ->
                    updateState { copy(settings = settings) }
                    // Reload hymn with new settings if already loaded
                    if (state.value.hymn != null) {
                        loadHymn(hymnId)
                    }
                }
        }
    }
    
    private fun updateParts(parts: Set<VoicePart>) {
        val newSettings = state.value.settings.copy(visibleParts = parts)
        updateState { copy(settings = newSettings) }
        saveSettings(newSettings)
        // Reload XML with new part settings
        if (state.value.hymn != null) {
            reloadFilteredXmlWithSettings(newSettings)
        }
    }
    
    private fun updateLanguages(languages: Set<Language>) {
        val newSettings = state.value.settings.copy(visibleLanguages = languages)
        updateState { copy(settings = newSettings) }
        saveSettings(newSettings)
        // Reload XML with new language settings
        if (state.value.hymn != null) {
            reloadFilteredXmlWithSettings(newSettings)
        }
    }
    
    private fun reloadFilteredXmlWithSettings(settings: com.example.jbchhymnbook.settings.domain.model.AppSettings) {
        viewModelScope.launch {
            val hymn = state.value.hymn ?: return@launch
            hymnRepository.getMusicXml(hymn.id)
                .onSuccess { musicXml ->
                    val filteredXml = filterMusicXmlUseCase(
                        musicXml,
                        settings.visibleParts,
                        settings.visibleLanguages,
                        settings.appLanguage
                    )
                    updateState { copy(filteredMusicXml = filteredXml) }
                }
        }
    }
    
    private fun updateAppLanguage(language: Language) {
        val newSettings = state.value.settings.copy(appLanguage = language)
        updateState { copy(settings = newSettings) }
        saveSettings(newSettings)
        // Reload XML with new app language (affects part name display)
        if (state.value.hymn != null) {
            reloadFilteredXmlWithSettings(newSettings)
        }
    }
    
    private fun saveSettings(settings: com.example.jbchhymnbook.settings.domain.model.AppSettings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
        }
    }
    
    private fun reloadFilteredXml() {
        viewModelScope.launch {
            val hymn = state.value.hymn ?: return@launch
            hymnRepository.getMusicXml(hymn.id)
                .onSuccess { musicXml ->
                    val filteredXml = filterMusicXmlUseCase(
                        musicXml,
                        state.value.settings.visibleParts,
                        state.value.settings.visibleLanguages,
                        state.value.settings.appLanguage
                    )
                    updateState { copy(filteredMusicXml = filteredXml) }
                }
        }
    }
}

