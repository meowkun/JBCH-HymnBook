package com.example.jbchhymnbook.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object TableOfContents : Route()
    
    @Serializable
    data class HymnList(val groupId: String) : Route()
    
    @Serializable
    data class HymnDisplay(val hymnId: String) : Route()
}

