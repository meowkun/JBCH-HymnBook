package com.example.jbchhymnbook.hymn.presentation.list

sealed interface HymnListIntent {
    data class LoadHymns(val groupId: String) : HymnListIntent
    data class SelectHymn(val hymnId: String) : HymnListIntent
}

