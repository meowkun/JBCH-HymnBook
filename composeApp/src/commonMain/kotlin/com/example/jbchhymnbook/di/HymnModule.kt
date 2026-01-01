package com.example.jbchhymnbook.di

import com.example.jbchhymnbook.hymn.data.datasource.HymnIndexDataSource
import com.example.jbchhymnbook.hymn.data.datasource.MusicXmlDataSource
import com.example.jbchhymnbook.hymn.data.parser.MusicXmlParser
import com.example.jbchhymnbook.hymn.data.repository.HymnRepositoryImpl
import com.example.jbchhymnbook.hymn.domain.repository.HymnRepository
import com.example.jbchhymnbook.hymn.domain.usecase.FilterMusicXmlUseCase
import com.example.jbchhymnbook.hymn.domain.usecase.GetHymnDetailUseCase
import com.example.jbchhymnbook.hymn.domain.usecase.GetHymnGroupsUseCase
import com.example.jbchhymnbook.hymn.domain.usecase.GetHymnsByGroupUseCase
import com.example.jbchhymnbook.hymn.presentation.display.HymnDisplayViewModel
import com.example.jbchhymnbook.hymn.presentation.list.HymnListViewModel
import com.example.jbchhymnbook.hymn.presentation.toc.TableOfContentsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val hymnModule = module {
    // Data Layer
    singleOf(::HymnIndexDataSource)
    singleOf(::MusicXmlDataSource)
    singleOf(::MusicXmlParser)
    singleOf(::HymnRepositoryImpl) bind HymnRepository::class
    
    // Domain Layer - Use Cases
    factoryOf(::GetHymnGroupsUseCase)
    factoryOf(::GetHymnsByGroupUseCase)
    factoryOf(::GetHymnDetailUseCase)
    factoryOf(::FilterMusicXmlUseCase)
    
    // Presentation Layer - ViewModels
    factoryOf(::TableOfContentsViewModel)
    factory { params -> HymnListViewModel(get(), params.get()) }
    factory { params -> HymnDisplayViewModel(get(), get(), get(), get(), params.get()) }
}

