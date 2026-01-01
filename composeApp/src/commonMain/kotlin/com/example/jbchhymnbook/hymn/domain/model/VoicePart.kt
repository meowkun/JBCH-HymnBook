package com.example.jbchhymnbook.hymn.domain.model

enum class VoicePart(val displayName: String, val partNames: List<String>) {
    SOPRANO("Soprano", listOf("Soprano", "S", "소프라노")),
    ALTO("Alto", listOf("Alto", "A", "알토")),
    TENOR("Tenor", listOf("Tenor", "T", "테너")),
    BASS("Bass", listOf("Bass", "B", "베이스"))
}

