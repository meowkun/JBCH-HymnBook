package com.example.jbchhymnbook.hymn.data.mapper

import com.example.jbchhymnbook.hymn.data.dto.HymnDto
import com.example.jbchhymnbook.hymn.data.dto.HymnGroupDto
import com.example.jbchhymnbook.hymn.domain.model.Hymn
import com.example.jbchhymnbook.hymn.domain.model.HymnGroup
import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart

object HymnMapper {
    fun toDomain(dto: HymnGroupDto): HymnGroup {
        val names = dto.names.mapKeys { entry ->
            Language.entries.find { it.code == entry.key } ?: Language.ENGLISH
        }
        return HymnGroup(
            id = dto.id,
            order = dto.order,
            names = names
        )
    }
    
    fun toDomain(dto: HymnDto): Hymn {
        val titles = dto.titles.mapKeys { entry ->
            Language.entries.find { it.code == entry.key } ?: Language.ENGLISH
        }
        val parts = dto.availableParts.mapNotNull { partName ->
            VoicePart.entries.find { voicePart ->
                voicePart.partNames.any { it.equals(partName, ignoreCase = true) }
            }
        }
        val languages = dto.availableLanguages.mapNotNull { lyricNumber ->
            Language.entries.find { it.lyricNumber == lyricNumber }
        }
        return Hymn(
            id = dto.id,
            number = dto.number,
            titles = titles,
            groupId = dto.groupId,
            musicXmlPath = dto.path,
            availableParts = parts,
            availableLanguages = languages
        )
    }
}

