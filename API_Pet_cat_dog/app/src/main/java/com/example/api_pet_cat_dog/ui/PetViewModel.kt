package com.example.api_pet_cat_dog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_pet_cat_dog.data.api.RetrofitInstance
import com.example.api_pet_cat_dog.data.model.Breed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val breeds: List<Breed>) : UiState()
    data class Error(val message: String) : UiState()
}

class PetViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchBreeds()
    }

    fun fetchBreeds() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val breeds = RetrofitInstance.api.getBreeds()
                _uiState.value = UiState.Success(breeds)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun searchBreeds(query: String) {
        if (query.isBlank()) {
            fetchBreeds()
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val breeds = RetrofitInstance.api.searchBreeds(query)
                _uiState.value = UiState.Success(breeds)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro na busca")
            }
        }
    }
}
