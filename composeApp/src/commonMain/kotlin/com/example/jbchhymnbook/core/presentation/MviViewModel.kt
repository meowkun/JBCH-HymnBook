package com.example.jbchhymnbook.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class MviViewModel<State, Intent>(
    initialState: State
) : ViewModel() {
    
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()
    
    protected fun updateState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }
    
    abstract fun onIntent(intent: Intent)
}

