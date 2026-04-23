package com.example.prova1_api_catdog.data.repository

import com.example.prova1_api_catdog.core.Result
import com.example.prova1_api_catdog.core.safeApiCall
import com.example.prova1_api_catdog.data.dto.BreedResponse
import com.example.prova1_api_catdog.data.dto.toDomainModel
import com.example.prova1_api_catdog.data.dto.toDomainModelList
import com.example.prova1_api_catdog.data.service.ApiService
import com.example.prova1_api_catdog.domain.model.Breed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BreedRepository(
    private val catApiService: ApiService,
    private val dogApiService: ApiService
) {

    private val cache = mutableMapOf<String, List<Breed>>()

    fun searchBreeds(query: String, petType: String): Flow<Result<List<Breed>>> = flow {
        val normalizedQuery = query.lowercase().trim()
        val cacheKey = "${petType}_$normalizedQuery"

        cache[cacheKey]?.let {
            emit(Result.Success(it))
            return@flow
        }

        val apiService = if (petType == "cat") catApiService else dogApiService

        safeApiCall {
            val response: List<BreedResponse> = apiService.searchBreedsByName(query)

            val breedsWithImages = response.map { breedResponse ->
                val images = apiService.searchImagesByBreedId(breedResponse.id, 1)
                val imageUrl = images.firstOrNull()?.url ?: ""
                breedResponse.toDomainModel().copy(imageUrl = imageUrl)
            }

            cache[cacheKey] = breedsWithImages
            breedsWithImages
        }.collect { result ->
            emit(result)
        }
    }

    fun clearCache() {
        cache.clear()
    }
}