package com.example.jbchhymnbook.settings.data

import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart
import com.example.jbchhymnbook.settings.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class AppSettingsData(
    val visibleParts: List<String> = VoicePart.entries.map { it.name },
    val visibleLanguages: List<String> = Language.entries.map { it.name },
    val appLanguage: String = Language.ENGLISH.name // Default, will be overridden by getDefaultSettings()
)

class SettingsDataSource {
    // For now, use in-memory storage. Will be replaced with DataStore later
    private var settings: AppSettingsData? = null // null means not initialized yet
    
    private fun getDefaultSettings(): AppSettingsData {
        val systemLanguage = getSystemLanguage()
        return AppSettingsData(
            visibleParts = VoicePart.entries.map { it.name },
            visibleLanguages = Language.entries.map { it.name },
            appLanguage = systemLanguage.name
        )
    }
    
    suspend fun getSettings(): AppSettingsData {
        // If settings haven't been initialized, return default with system language
        if (settings == null) {
            settings = getDefaultSettings()
        }
        return settings!!
    }
    
    suspend fun updateSettings(newSettings: AppSettingsData) {
        settings = newSettings
    }
    
    fun toDomain(data: AppSettingsData): AppSettings {
        val parts = data.visibleParts.mapNotNull { name ->
            VoicePart.entries.find { it.name == name }
        }.toSet()
        val languages = data.visibleLanguages.mapNotNull { name ->
            Language.entries.find { it.name == name }
        }.toSet()
        val appLanguage = Language.entries.find { it.name == data.appLanguage } 
            ?: getSystemLanguage() // Fallback to system language if not found
        
        return AppSettings(
            visibleParts = parts.ifEmpty { VoicePart.entries.toSet() },
            visibleLanguages = languages.ifEmpty { Language.entries.toSet() },
            appLanguage = appLanguage
        )
    }
    
    fun fromDomain(settings: AppSettings): AppSettingsData {
        return AppSettingsData(
            visibleParts = settings.visibleParts.map { it.name },
            visibleLanguages = settings.visibleLanguages.map { it.name },
            appLanguage = settings.appLanguage.name
        )
    }
}

