package com.example.jbchhymnbook.hymn.domain.model

data class Hymn(
    val id: String,
    val number: Int,
    val titles: Map<Language, String>,
    val groupId: String,
    val musicXmlPath: String,
    val availableParts: List<VoicePart>,
    val availableLanguages: List<Language>
)

