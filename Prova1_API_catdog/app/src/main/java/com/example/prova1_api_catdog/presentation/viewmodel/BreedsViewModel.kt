package com.example.prova1_api_catdog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.prova1_api_catdog.core.Result
import com.example.prova1_api_catdog.data.repository.BreedRepository
import com.example.prova1_api_catdog.domain.model.Breed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val breeds: List<Breed> = emptyList(),
    val randomPhotos: List<String> = emptyList(), // Adicionado para a Home
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isEmptyResult: Boolean = false,
    val isFirstLoad: Boolean = true
)

class BreedsViewModel(
    private val repository: BreedRepository,
    private var petType: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        loadHomePhotos() // Carrega as 4 fotos ao iniciar
        setupSearchDebounce()
    }

    private fun loadHomePhotos() {
        viewModelScope.launch {
            repository.getRandomPhotos().collect { result ->
                if (result is Result.Success) {
                    _uiState.update { it.copy(randomPhotos = result.data) }
                }
            }
        }
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
        searchJob?.cancel() // Cancela a busca anterior se houver uma nova
        
        if (query.isBlank()) {
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    breeds = emptyList(),
                    errorMessage = null,
                    searchQuery = query,
                    isEmptyResult = false,
                    isFirstLoad = true // Volta para o estado inicial
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            repository.searchBreeds(query, petType).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = true,
                                errorMessage = null,
                                isEmptyResult = false
                            )
                        }
                    }
                    is Result.Success -> {
                        val breedsList = result.data
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                breeds = breedsList,
                                errorMessage = null,
                                isEmptyResult = breedsList.isEmpty(),
                                isFirstLoad = false
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { 
                            it.copy(
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
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updatePetType(newType: String) {
        petType = newType
        // Mantemos as fotos da home, apenas resetamos o estado de busca
        _searchQuery.value = ""
        _uiState.update { 
            it.copy(
                breeds = emptyList(),
                searchQuery = "",
                isFirstLoad = true 
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    companion object {
        fun Factory(repository: BreedRepository, petType: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BreedsViewModel(repository, petType) as T
                }
            }
        }
    }
}