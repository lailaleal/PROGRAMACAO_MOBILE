package com.example.prova1_api_catdog.data.repository

import com.example.prova1_api_catdog.core.Result
import com.example.prova1_api_catdog.core.safeApiCall
import com.example.prova1_api_catdog.data.dto.BreedResponse
import com.example.prova1_api_catdog.data.dto.toDomainModelList
import com.example.prova1_api_catdog.data.service.ApiService
import com.example.prova1_api_catdog.domain.model.Breed
import kotlinx.coroutines.flow.Flow

class BreedRepository(
    private val apiService: ApiService
) {

    // Buscar todas as raças
    fun getAllBreeds(): Flow<Result<List<Breed>>> = safeApiCall {
        val response: List<BreedResponse> = apiService.getAllBreeds()
        response.toDomainModelList()
    }

    // Buscar raça por nome
    fun searchBreeds(query: String): Flow<Result<List<Breed>>> = safeApiCall {
        val response: List<BreedResponse> = apiService.searchBreeds(query)
        response.toDomainModelList()
    }
}