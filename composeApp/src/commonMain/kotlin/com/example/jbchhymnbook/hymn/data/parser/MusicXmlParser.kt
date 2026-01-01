package com.example.jbchhymnbook.hymn.data.parser

import com.example.jbchhymnbook.hymn.domain.model.Language
import com.example.jbchhymnbook.hymn.domain.model.VoicePart

class MusicXmlParser {
    
    /**
     * Maps instrument names to SATB voice parts
     * Common mappings:
     * - Violin 1 / 바이올린1 -> Soprano
     * - Violin 2 / 바이올린2 -> Alto
     * - Viola / 비올라 -> Tenor
     * - Cello / 비올론첼로 -> Bass
     */
    /**
     * Maps instrument names to SATB voice parts
     * Common mappings:
     * - Violin 1 / 바이올린1 -> Soprano
     * - Violin 2 / 바이올린2 -> Alto
     * - Viola / 비올라 -> Tenor
     * - Cello / 비올론첼로 -> Bass
     */
    private fun mapInstrumentToVoicePart(partName: String): VoicePart? {
        val normalizedName = partName.trim().lowercase()
        
        return when {
            // Violin 2 / 바이올린2 -> Alto (check this first to avoid matching Violin 1)
            (normalizedName.contains("violin") && normalizedName.contains("2")) ||
            (normalizedName.contains("바이올린") && normalizedName.contains("2")) ||
            normalizedName == "vln.2" || normalizedName == "vln2" || normalizedName == "violin 2" ||
            normalizedName == "바이올린2" -> VoicePart.ALTO
            
            // Violin 1 / 바이올린1 -> Soprano
            (normalizedName.contains("violin") && normalizedName.contains("1")) ||
            (normalizedName.contains("바이올린") && normalizedName.contains("1")) ||
            normalizedName == "vln.1" || normalizedName == "vln1" || normalizedName == "violin 1" ||
            normalizedName == "바이올린1" ||
            // Just "Violin" or "바이올린" (without number) defaults to Soprano
            (normalizedName == "violin" || normalizedName == "바이올린") -> VoicePart.SOPRANO
            
            // Viola / 비올라 -> Tenor
            (normalizedName.contains("viola") && !normalizedName.contains("cello")) ||
            (normalizedName.contains("비올라") && !normalizedName.contains("첼로")) ||
            normalizedName == "vla" || normalizedName == "vla." || normalizedName.trim() == "비올라" -> VoicePart.TENOR
            
            // Cello / 비올론첼로 -> Bass
            normalizedName.contains("cello") ||
            normalizedName.contains("첼로") ||
            normalizedName.contains("비올론") ||
            normalizedName == "vc" || normalizedName == "vc." || normalizedName == "vcl" ||
            normalizedName == "비올론첼로" -> VoicePart.BASS
            
            // Direct SATB names (already handled by VoicePart.partNames, but included for completeness)
            VoicePart.SOPRANO.partNames.any { it.equals(partName, ignoreCase = true) } -> VoicePart.SOPRANO
            VoicePart.ALTO.partNames.any { it.equals(partName, ignoreCase = true) } -> VoicePart.ALTO
            VoicePart.TENOR.partNames.any { it.equals(partName, ignoreCase = true) } -> VoicePart.TENOR
            VoicePart.BASS.partNames.any { it.equals(partName, ignoreCase = true) } -> VoicePart.BASS
            
            else -> null
        }
    }
    
    fun extractParts(musicXml: String): List<VoicePart> {
        val parts = mutableSetOf<VoicePart>()
        
        // Extract part names using regex
        val partNamePattern = Regex("<part-name[^>]*>([^<]+)</part-name>", RegexOption.IGNORE_CASE)
        val matches = partNamePattern.findAll(musicXml)
        
        matches.forEach { matchResult ->
            val partName = matchResult.groupValues[1].trim()
            
            // Try to map instrument name to voice part first
            val mappedVoicePart = mapInstrumentToVoicePart(partName)
            if (mappedVoicePart != null) {
                parts.add(mappedVoicePart)
            } else {
                // Fall back to direct name matching
                VoicePart.entries.forEach { voicePart ->
                    if (voicePart.partNames.any { it.equals(partName, ignoreCase = true) }) {
                        parts.add(voicePart)
                    }
                }
            }
        }
        
        return parts.toList()
    }
    
    fun extractAvailableLanguages(musicXml: String): List<Language> {
        val languages = mutableSetOf<Language>()
        
        // Extract lyric numbers using regex
        val lyricPattern = Regex("""<lyric[^>]*number\s*=\s*["'](\d+)["']""", RegexOption.IGNORE_CASE)
        val matches = lyricPattern.findAll(musicXml)
        
        matches.forEach { matchResult ->
            val lyricNumber = matchResult.groupValues[1].toIntOrNull()
            if (lyricNumber != null) {
                Language.entries.find { it.lyricNumber == lyricNumber }?.let {
                    languages.add(it)
                }
            }
        }
        
        return languages.toList()
    }
    
    fun filterMusicXml(
        musicXml: String,
        visibleParts: Set<VoicePart>,
        visibleLanguages: Set<Language>,
        appLanguage: Language? = null
    ): String {
        var filteredXml = musicXml
        
        // Extract part IDs to keep
        val partIdsToKeep = mutableSetOf<String>()
        val scorePartPattern = Regex("""<score-part\s+id\s*=\s*["']([^"']+)["'][^>]*>([\s\S]*?)</score-part>""", RegexOption.IGNORE_CASE)
        val scorePartMatches = scorePartPattern.findAll(musicXml)
        
        scorePartMatches.forEach { matchResult ->
            val partId = matchResult.groupValues[1]
            val partContent = matchResult.groupValues[2]
            val partNameMatch = Regex("<part-name[^>]*>([^<]+)</part-name>", RegexOption.IGNORE_CASE).find(partContent)
            val partName = partNameMatch?.groupValues?.get(1)?.trim() ?: ""
            
            // Try to map instrument name to voice part
            val mappedVoicePart = mapInstrumentToVoicePart(partName)
            
            val shouldKeep = if (mappedVoicePart != null) {
                // Use the mapped voice part
                visibleParts.contains(mappedVoicePart)
            } else {
                // Fall back to direct name matching
                VoicePart.entries.any { voicePart ->
                    visibleParts.contains(voicePart) &&
                    voicePart.partNames.any { it.equals(partName, ignoreCase = true) }
                }
            }
            
            if (shouldKeep) {
                partIdsToKeep.add(partId)
            }
        }
        
        // If no parts matched, keep all parts (don't filter by parts)
        // This handles cases where XML uses instrument names instead of voice parts
        val shouldFilterParts = partIdsToKeep.isNotEmpty()
        
        if (shouldFilterParts) {
            // Remove parts not in visibleParts
            val partPattern = Regex("""<part\s+id\s*=\s*["']([^"']+)["'][^>]*>([\s\S]*?)</part>""", RegexOption.IGNORE_CASE)
            filteredXml = partPattern.replace(filteredXml) { matchResult ->
                val partId = matchResult.groupValues[1]
                if (partIdsToKeep.contains(partId)) {
                    matchResult.value
                } else {
                    ""
                }
            }
            
            // Remove score-part entries not in visibleParts
            filteredXml = scorePartPattern.replace(filteredXml) { matchResult ->
                val partId = matchResult.groupValues[1]
                if (partIdsToKeep.contains(partId)) {
                    matchResult.value
                } else {
                    ""
                }
            }
        }
        // If shouldFilterParts is false, keep all parts (no filtering)
        
        // Copy lyrics from first part to all other parts that don't have lyrics
        // This ensures lyrics exist in all parts
        // Only do this if we have parts to process
        if (partIdsToKeep.isNotEmpty() || !shouldFilterParts) {
            try {
                filteredXml = copyLyricsToAllParts(filteredXml)
                
                // If multiple parts are visible, show lyrics only in the first visible part
                if (partIdsToKeep.size > 1) {
                    val firstVisiblePartId = partIdsToKeep.firstOrNull()
                    if (firstVisiblePartId != null) {
                        filteredXml = removeLyricsFromPartsExceptFirst(filteredXml, firstVisiblePartId, partIdsToKeep)
                    }
                }
            } catch (e: Exception) {
                // If lyric copying fails, return XML without lyric modifications
                // This prevents breaking the XML structure
                // Log error but continue with original XML
            }
        }
        
        // Filter lyrics by language (only if languages are specified)
        if (visibleLanguages.isNotEmpty() && visibleLanguages.size < Language.entries.size) {
            val visibleLyricNumbers = visibleLanguages.map { it.lyricNumber }.toSet()
            val lyricPattern = Regex("""<lyric([^>]*number\s*=\s*["'](\d+)["'][^>]*)>([\s\S]*?)</lyric>""", RegexOption.IGNORE_CASE)
            filteredXml = lyricPattern.replace(filteredXml) { matchResult ->
                val lyricNumber = matchResult.groupValues[2].toIntOrNull()
                if (lyricNumber != null && visibleLyricNumbers.contains(lyricNumber)) {
                    matchResult.value
                } else {
                    ""
                }
            }
        }
        // If all languages are visible or empty set, keep all lyrics
        
        // Update part-name elements with language-specific display text
        if (appLanguage != null) {
            filteredXml = updatePartNamesWithLanguage(filteredXml, appLanguage)
        }
        
        return filteredXml
    }
    
    /**
     * Updates part-name elements with language-specific display text
     */
    private fun updatePartNamesWithLanguage(musicXml: String, language: Language): String {
        val langCode = language.code
        var result = musicXml
        
        // Find all score-part elements with part-name-display
        val scorePartPattern = Regex("""(<score-part\s+id\s*=\s*["'][^"']+["'][^>]*>)([\s\S]*?)(</score-part>)""", RegexOption.IGNORE_CASE)
        
        result = scorePartPattern.replace(result) { partMatch ->
            val partContent = partMatch.groupValues[2]
            val partNameDisplayPattern = Regex("""<part-name-display[^>]*>([\s\S]*?)</part-name-display>""", RegexOption.IGNORE_CASE)
            val displayMatch = partNameDisplayPattern.find(partContent)
            
            if (displayMatch != null) {
                val displayContent = displayMatch.groupValues[1]
                val displayTextPattern = Regex("""<display-text\s+xml:lang\s*=\s*["']([^"']+)["'][^>]*>([^<]+)</display-text>""", RegexOption.IGNORE_CASE)
                // Find all display-text elements and get the one matching the language
                val allDisplayTexts = displayTextPattern.findAll(displayContent)
                val langMatch = allDisplayTexts.firstOrNull { it.groupValues[1] == langCode }
                
                if (langMatch != null) {
                    val displayText = langMatch.groupValues[2]
                    // Update part-name with the language-specific text
                    val updatedContent = partContent.replace(
                        Regex("<part-name[^>]*>([^<]+)</part-name>", RegexOption.IGNORE_CASE),
                        "<part-name>$displayText</part-name>"
                    )
                    "${partMatch.groupValues[1]}$updatedContent${partMatch.groupValues[3]}"
                } else {
                    // Fallback to first display-text if language not found
                    val firstMatch = allDisplayTexts.firstOrNull()
                    if (firstMatch != null) {
                        val displayText = firstMatch.groupValues[2]
                        val updatedContent = partContent.replace(
                            Regex("<part-name[^>]*>([^<]+)</part-name>", RegexOption.IGNORE_CASE),
                            "<part-name>$displayText</part-name>"
                        )
                        "${partMatch.groupValues[1]}$updatedContent${partMatch.groupValues[3]}"
                    } else {
                        partMatch.value
                    }
                }
            } else {
                partMatch.value
            }
        }
        
        return result
    }
    
    /**
     * Copies lyrics from the first part (usually Soprano) to all other parts
     * that don't have lyrics. This ensures lyrics exist in all parts.
     */
    private fun copyLyricsToAllParts(musicXml: String): String {
        // Find the first part with lyrics
        val partPattern = Regex("""<part\s+id\s*=\s*["']([^"']+)["'][^>]*>([\s\S]*?)</part>""", RegexOption.IGNORE_CASE)
        val parts = partPattern.findAll(musicXml).toList()
        
        if (parts.isEmpty()) return musicXml
        
        // Find first part with lyrics
        val firstPartWithLyrics = parts.firstOrNull { part ->
            part.groupValues[2].contains("<lyric", ignoreCase = true)
        } ?: return musicXml
        
        val lyricsFromFirstPart = extractLyricsFromPart(firstPartWithLyrics.groupValues[2])
        if (lyricsFromFirstPart.isEmpty()) return musicXml
        
        // Copy lyrics to parts that don't have them
        // Build result by processing each part individually to avoid replacement issues
        val result = StringBuilder()
        var lastIndex = 0
        
        parts.forEach { partMatch ->
            val partId = partMatch.groupValues[1]
            val partContent = partMatch.groupValues[2]
            val partStart = partMatch.range.first
            val partEnd = partMatch.range.last + 1
            
            // Add everything before this part
            result.append(musicXml.substring(lastIndex, partStart))
            
            // Check if this part already has lyrics
            if (partContent.contains("<lyric", ignoreCase = true)) {
                // Keep part as is
                result.append(partMatch.value)
            } else {
                // Add lyrics to notes in this part
                val updatedPart = addLyricsToPart(partContent, lyricsFromFirstPart)
                // Reconstruct the part tag with updated content
                val partTagMatch = Regex("""<part\s+id\s*=\s*["']([^"']+)["'][^>]*>""", RegexOption.IGNORE_CASE).find(partMatch.value)
                val partTag = partTagMatch?.value ?: "<part id=\"$partId\">"
                result.append(partTag)
                result.append(updatedPart)
                result.append("</part>")
            }
            
            lastIndex = partEnd
        }
        
        // Add remaining content after last part
        if (lastIndex < musicXml.length) {
            result.append(musicXml.substring(lastIndex))
        }
        
        return result.toString()
    }
    
    /**
     * Extracts lyrics from a part's content, mapping them by measure and note position
     */
    private fun extractLyricsFromPart(partContent: String): List<String> {
        val lyrics = mutableListOf<String>()
        // Match full lyric element including all nested content
        val lyricPattern = Regex("""<lyric[^>]*>[\s\S]*?</lyric>""", RegexOption.IGNORE_CASE)
        lyricPattern.findAll(partContent).forEach { match ->
            // match.value gives us the full matched string including tags
            val fullLyric = match.value
            if (fullLyric.contains("</lyric>", ignoreCase = true)) {
                lyrics.add(fullLyric)
            }
        }
        return lyrics
    }
    
    /**
     * Adds lyrics to all notes in a part that don't have lyrics
     */
    private fun addLyricsToPart(partContent: String, lyrics: List<String>): String {
        if (lyrics.isEmpty()) return partContent
        
        var result = StringBuilder()
        var lyricIndex = 0
        var currentPos = 0
        
        // Find all </note> tags and check if the note before it has lyrics
        val noteEndPattern = Regex("</note>", RegexOption.IGNORE_CASE)
        val matches = noteEndPattern.findAll(partContent)
        
        for (match in matches) {
            val noteEndPos = match.range.first
            
            // Find the start of this note by looking backwards for <note
            val noteStartPos = partContent.lastIndexOf("<note", noteEndPos, ignoreCase = true)
            if (noteStartPos == -1) {
                result.append(partContent.substring(currentPos, noteEndPos + 6))
                currentPos = noteEndPos + 6
                continue
            }
            
            // Extract the note content
            val noteContent = partContent.substring(noteStartPos, noteEndPos + 6) // +6 for "</note>"
            
            // Add everything before this note
            result.append(partContent.substring(currentPos, noteStartPos))
            
            // Check if this note already has lyrics
            if (noteContent.contains("<lyric", ignoreCase = true)) {
                // Keep note as is
                result.append(noteContent)
            } else {
                // Insert lyric before </note>
                val lyric = lyrics[lyricIndex % lyrics.size]
                // Validate that lyric is a complete XML element
                if (lyric.trim().startsWith("<lyric", ignoreCase = true) && 
                    lyric.trim().endsWith("</lyric>", ignoreCase = true)) {
                    result.append(partContent.substring(noteStartPos, noteEndPos))
                    result.append(lyric)
                    result.append("</note>")
                    lyricIndex++
                } else {
                    // If lyric is malformed, skip it and keep note as is
                    result.append(noteContent)
                }
            }
            
            currentPos = noteEndPos + 6
        }
        
        // Add remaining content
        if (currentPos < partContent.length) {
            result.append(partContent.substring(currentPos))
        }
        
        return result.toString()
    }
    
    /**
     * Removes lyrics from all parts except the first visible part
     */
    private fun removeLyricsFromPartsExceptFirst(
        musicXml: String,
        firstPartId: String,
        visiblePartIds: Set<String>
    ): String {
        val partPattern = Regex("""<part\s+id\s*=\s*["']([^"']+)["'][^>]*>([\s\S]*?)</part>""", RegexOption.IGNORE_CASE)
        
        return partPattern.replace(musicXml) { matchResult ->
            val partId = matchResult.groupValues[1]
            val partContent = matchResult.groupValues[2]
            
            // Keep lyrics in first part, remove from others
            if (partId == firstPartId || !visiblePartIds.contains(partId)) {
                matchResult.value
            } else {
                // Remove all lyrics from this part
                val lyricPattern = Regex("""<lyric[^>]*>[\s\S]*?</lyric>""", RegexOption.IGNORE_CASE)
                val cleanedContent = lyricPattern.replace(partContent, "")
                matchResult.value.replace(partContent, cleanedContent)
            }
        }
    }
}

