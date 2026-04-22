package com.example.prova1_api_catdog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova1_api_catdog.core.Result
import com.example.prova1_api_catdog.data.repository.BreedRepository
import com.example.prova1_api_catdog.domain.model.Breed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// UI State - Representa o estado da tela
data class UiState(
    val isLoading: Boolean = false,
    val breeds: List<Breed> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class BreedsViewModel(
    private val repository: BreedRepository
) : ViewModel() {

    // Estado privado (pode ser modificado)
    private val _uiState = MutableStateFlow(UiState())

    // Estado público (apenas leitura)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Buscar todas as raças
    fun getAllBreeds() {
        viewModelScope.launch {
            repository.getAllBreeds().collectLatest { result ->
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
                            errorMessage = result.exception.message ?: "Erro desconhecido"
                        )
                    }
                }
            }
        }
    }

    // Buscar raça por nome
    fun searchBreeds(query: String) {
        // Atualiza o searchQuery no UiState
        _uiState.value = _uiState.value.copy(searchQuery = query)

        // Se a busca estiver vazia, limpa os resultados
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(breeds = emptyList())
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

    // Limpar erro
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}