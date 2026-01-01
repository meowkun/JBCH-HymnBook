package com.example.jbchhymnbook.settings.domain.repository

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.settings.domain.model.AppSettings

interface SettingsRepository {
    suspend fun getSettings(): Result<AppSettings>
    suspend fun updateSettings(settings: AppSettings): Result<Unit>
}

