package com.example.jbchhymnbook.hymn.domain.usecase

import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart
import com.example.jbchhymnbook.hymn.data.parser.MusicXmlParser

class FilterMusicXmlUseCase(
    private val musicXmlParser: MusicXmlParser
) {
    operator fun invoke(
        musicXml: String,
        visibleParts: Set<VoicePart>,
        visibleLanguages: Set<Language>,
        appLanguage: Language? = null
    ): String {
        return musicXmlParser.filterMusicXml(musicXml, visibleParts, visibleLanguages, appLanguage)
    }
}

