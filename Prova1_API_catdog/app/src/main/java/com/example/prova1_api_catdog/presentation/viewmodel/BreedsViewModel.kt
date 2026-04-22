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
    val searchQuery: String = "",
    val isEmptyResult: Boolean = false,
    val isFirstLoad: Boolean = true
)

class BreedsViewModel(
    private val repository: BreedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.value = UiState(
                isLoading = false,
                breeds = emptyList(),
                errorMessage = null,
                searchQuery = query,
                isEmptyResult = false,
                isFirstLoad = false
            )
            return
        }

        viewModelScope.launch {
            repository.searchBreeds(query).collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null,
                            isEmptyResult = false
                        )
                    }
                    is Result.Success -> {
                        val breedsList = result.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            breeds = breedsList,
                            errorMessage = null,
                            isEmptyResult = breedsList.isEmpty(),
                            isFirstLoad = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Erro na busca",
                            isEmptyResult = false,
                            isFirstLoad = false
                        )
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}