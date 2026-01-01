package com.example.jbchhymnbook.hymn.presentation.list

import com.example.jbchhymnbook.hymn.domain.model.Hymn

data class HymnListState(
    val isLoading: Boolean = true,
    val hymns: List<Hymn> = emptyList(),
    val error: String? = null
)

