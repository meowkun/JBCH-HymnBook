package com.example.jbchhymnbook.hymn.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HymnIndexDto(
    val groups: List<HymnGroupDto>,
    val hymns: List<HymnDto>
)

@Serializable
data class HymnGroupDto(
    val id: String,
    val order: Int,
    val names: Map<String, String>
)

@Serializable
data class HymnDto(
    val id: String,
    val number: Int,
    val groupId: String,
    val path: String,
    val titles: Map<String, String>,
    val availableParts: List<String>,
    val availableLanguages: List<Int>
)

