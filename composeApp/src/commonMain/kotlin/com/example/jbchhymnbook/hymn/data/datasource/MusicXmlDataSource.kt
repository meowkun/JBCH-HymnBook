package com.example.jbchhymnbook.hymn.data.datasource

class MusicXmlDataSource {
    suspend fun loadMusicXml(path: String): String {
        // Load the .xml file directly (no unzipping needed)
        val resourcePath = "hymns/$path"
        // Use platform-specific resource loading
        return loadResourceFile(resourcePath)
    }
}

