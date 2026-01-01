package com.example.jbchhymnbook.hymn.domain.model

data class HymnGroup(
    val id: String,
    val order: Int,
    val names: Map<Language, String>
)

