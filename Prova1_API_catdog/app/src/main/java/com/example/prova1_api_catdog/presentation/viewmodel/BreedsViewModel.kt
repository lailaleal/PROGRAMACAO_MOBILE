package com.example.prova1_api_catdog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova1_api_catdog.core.Result
import com.example.prova1_api_catdog.data.repository.BreedRepository
import com.example.prova1_api_catdog.domain.model.Breed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val breeds: List<Breed> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class BreedsViewModel(
    private val repository: BreedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Campo privado para controlar a busca com debounce
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Observa as mudanças no searchQuery com debounce
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)  // Aguarda 500ms após o usuário parar de digitar
                .distinctUntilChanged()  // Ignora se o valor não mudou
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                breeds = emptyList(),
                isLoading = false,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            repository.searchBreeds(query).collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            breeds = result.data,
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Erro na busca"
                        )
                    }
                }
            }
        }
    }

    // Função chamada pela UI quando o usuário digita
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}