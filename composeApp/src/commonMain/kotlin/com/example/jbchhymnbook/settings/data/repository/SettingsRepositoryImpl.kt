package com.example.jbchhymnbook.settings.data.repository

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.settings.data.SettingsDataSource
import com.example.jbchhymnbook.settings.domain.model.AppSettings
import com.example.jbchhymnbook.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val dataSource: SettingsDataSource
) : SettingsRepository {
    
    override suspend fun getSettings(): Result<AppSettings> {
        return try {
            val data = dataSource.getSettings()
            val settings = dataSource.toDomain(data)
            Result.Success(settings)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateSettings(settings: AppSettings): Result<Unit> {
        return try {
            val data = dataSource.fromDomain(settings)
            dataSource.updateSettings(data)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

