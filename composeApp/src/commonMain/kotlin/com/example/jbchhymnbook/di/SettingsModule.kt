package com.example.jbchhymnbook.di

import com.example.jbchhymnbook.settings.data.SettingsDataSource
import com.example.jbchhymnbook.settings.data.repository.SettingsRepositoryImpl
import com.example.jbchhymnbook.settings.domain.repository.SettingsRepository
import com.example.jbchhymnbook.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    // Data Layer
    singleOf(::SettingsDataSource)
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
    
    // Presentation Layer - ViewModels
    factoryOf(::SettingsViewModel)
}

