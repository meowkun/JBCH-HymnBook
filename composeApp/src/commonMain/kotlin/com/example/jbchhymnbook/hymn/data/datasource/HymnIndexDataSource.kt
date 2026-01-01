package com.example.jbchhymnbook.hymn.data.datasource

import com.example.jbchhymnbook.hymn.data.dto.HymnIndexDto
import kotlinx.serialization.json.Json

class HymnIndexDataSource {
    suspend fun loadHymnIndex(): HymnIndexDto {
        return try {
            // Use platform-specific resource loading
            val jsonString = loadResourceFile("hymn_index.json")
            val index = Json.decodeFromString<HymnIndexDto>(jsonString)
            // If parsing succeeds but groups are empty, throw to show error
            if (index.groups.isEmpty() && index.hymns.isEmpty()) {
                throw IllegalStateException("Hymn index is empty")
            }
            index
        } catch (e: Exception) {
            // Re-throw to see the actual error in the UI
            throw IllegalStateException("Failed to load hymn index: ${e.message}", e)
        }
    }
}

// Expect function for platform-specific resource file loading
expect suspend fun loadResourceFile(path: String): String

