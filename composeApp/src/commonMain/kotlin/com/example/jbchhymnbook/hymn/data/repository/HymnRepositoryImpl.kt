package com.example.jbchhymnbook.hymn.data.repository

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.hymn.data.datasource.HymnIndexDataSource
import com.example.jbchhymnbook.hymn.data.datasource.MusicXmlDataSource
import com.example.jbchhymnbook.hymn.data.parser.MusicXmlParser
import com.example.jbchhymnbook.hymn.data.mapper.HymnMapper
import com.example.jbchhymnbook.hymn.domain.model.Hymn
import com.example.jbchhymnbook.hymn.domain.model.HymnGroup
import com.example.jbchhymnbook.hymn.domain.repository.HymnRepository

class HymnRepositoryImpl(
    private val hymnIndexDataSource: HymnIndexDataSource,
    private val musicXmlDataSource: MusicXmlDataSource,
    private val musicXmlParser: MusicXmlParser
) : HymnRepository {
    
    override suspend fun getHymnGroups(): Result<List<HymnGroup>> {
        return try {
            val index = hymnIndexDataSource.loadHymnIndex()
            val groups = index.groups.map { HymnMapper.toDomain(it) }
            Result.Success(groups)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getHymnsByGroup(groupId: String): Result<List<Hymn>> {
        return try {
            val index = hymnIndexDataSource.loadHymnIndex()
            val hymns = index.hymns
                .filter { it.groupId == groupId }
                .map { HymnMapper.toDomain(it) }
            Result.Success(hymns)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getHymnById(id: String): Result<Hymn> {
        return try {
            val index = hymnIndexDataSource.loadHymnIndex()
            val hymnDto = index.hymns.find { it.id == id }
                ?: return Result.Error(IllegalArgumentException("Hymn not found: $id"))
            
            // Load MusicXML to extract actual parts and languages
            val musicXml = musicXmlDataSource.loadMusicXml(hymnDto.path)
            val availableParts = musicXmlParser.extractParts(musicXml)
            val availableLanguages = musicXmlParser.extractAvailableLanguages(musicXml)
            
            val hymn = HymnMapper.toDomain(hymnDto).copy(
                availableParts = availableParts.ifEmpty { HymnMapper.toDomain(hymnDto).availableParts },
                availableLanguages = availableLanguages.ifEmpty { HymnMapper.toDomain(hymnDto).availableLanguages }
            )
            
            Result.Success(hymn)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getMusicXml(hymnId: String): Result<String> {
        return try {
            val index = hymnIndexDataSource.loadHymnIndex()
            val hymnDto = index.hymns.find { it.id == hymnId }
                ?: return Result.Error(IllegalArgumentException("Hymn not found: $hymnId"))
            
            val musicXml = musicXmlDataSource.loadMusicXml(hymnDto.path)
            Result.Success(musicXml)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

