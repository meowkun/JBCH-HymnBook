package com.example.jbchhymnbook.hymn.presentation.toc

import com.example.jbchhymnbook.hymn.domain.model.HymnGroup

data class TableOfContentsState(
    val isLoading: Boolean = true,
    val groups: List<HymnGroup> = emptyList(),
    val error: String? = null
)

