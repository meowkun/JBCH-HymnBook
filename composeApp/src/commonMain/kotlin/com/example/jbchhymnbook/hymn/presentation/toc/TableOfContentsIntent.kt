package com.example.jbchhymnbook.hymn.presentation.toc

sealed interface TableOfContentsIntent {
    data object LoadGroups : TableOfContentsIntent
    data class SelectGroup(val groupId: String) : TableOfContentsIntent
}

