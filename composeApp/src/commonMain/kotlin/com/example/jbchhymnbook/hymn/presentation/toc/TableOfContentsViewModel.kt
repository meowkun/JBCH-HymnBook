package com.example.jbchhymnbook.hymn.presentation.toc

import androidx.lifecycle.viewModelScope
import com.example.jbchhymnbook.core.presentation.MviViewModel
import com.example.jbchhymnbook.hymn.domain.usecase.GetHymnGroupsUseCase
import kotlinx.coroutines.launch

class TableOfContentsViewModel(
    private val getHymnGroupsUseCase: GetHymnGroupsUseCase
) : MviViewModel<TableOfContentsState, TableOfContentsIntent>(
    TableOfContentsState()
) {
    
    init {
        onIntent(TableOfContentsIntent.LoadGroups)
    }
    
    override fun onIntent(intent: TableOfContentsIntent) {
        when (intent) {
            is TableOfContentsIntent.LoadGroups -> loadGroups()
            is TableOfContentsIntent.SelectGroup -> {
                // Navigation handled by screen
            }
        }
    }
    
    private fun loadGroups() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            getHymnGroupsUseCase()
                .onSuccess { groups ->
                    updateState { copy(isLoading = false, groups = groups) }
                }
                .onFailure { error ->
                    updateState { 
                        copy(
                            isLoading = false, 
                            error = error.message ?: "Failed to load groups"
                        ) 
                    }
                }
        }
    }
}

