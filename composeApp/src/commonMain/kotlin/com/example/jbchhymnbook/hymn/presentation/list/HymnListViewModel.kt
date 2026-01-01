package com.example.jbchhymnbook.hymn.presentation.list

import androidx.lifecycle.viewModelScope
import com.example.jbchhymnbook.core.presentation.MviViewModel
import com.example.jbchhymnbook.hymn.domain.usecase.GetHymnsByGroupUseCase
import kotlinx.coroutines.launch

class HymnListViewModel(
    private val getHymnsByGroupUseCase: GetHymnsByGroupUseCase,
    private val groupId: String
) : MviViewModel<HymnListState, HymnListIntent>(
    HymnListState()
) {
    
    init {
        onIntent(HymnListIntent.LoadHymns(groupId))
    }
    
    override fun onIntent(intent: HymnListIntent) {
        when (intent) {
            is HymnListIntent.LoadHymns -> loadHymns(intent.groupId)
            is HymnListIntent.SelectHymn -> {
                // Navigation handled by screen
            }
        }
    }
    
    private fun loadHymns(groupId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            getHymnsByGroupUseCase(groupId)
                .onSuccess { hymns ->
                    updateState { copy(isLoading = false, hymns = hymns) }
                }
                .onFailure { error ->
                    updateState { 
                        copy(
                            isLoading = false, 
                            error = error.message ?: "Failed to load hymns"
                        ) 
                    }
                }
        }
    }
}

