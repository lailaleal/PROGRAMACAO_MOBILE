package com.example.prova1_api_catdog.data.repository

import com.example.prova1_api_catdog.core.Result
import com.example.prova1_api_catdog.core.safeApiCall
import com.example.prova1_api_catdog.data.dto.BreedResponse
import com.example.prova1_api_catdog.data.dto.toDomainModelList
import com.example.prova1_api_catdog.data.service.ApiService
import com.example.prova1_api_catdog.domain.model.Breed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BreedRepository(
    private val apiService: ApiService
) {

    private val cache = mutableMapOf<String, List<Breed>>()

    fun getAllBreeds(): Flow<Result<List<Breed>>> = flow {
        val cacheKey = "all_breeds"

        cache[cacheKey]?.let {
            emit(Result.Success(it))
            return@flow
        }

        safeApiCall {
            val response: List<BreedResponse> = apiService.getAllBreeds()
            val breeds = response.toDomainModelList()
            cache[cacheKey] = breeds
            breeds
        }.collect { result ->
            emit(result)
        }
    }

    fun searchBreeds(query: String): Flow<Result<List<Breed>>> = flow {
        val normalizedQuery = query.lowercase().trim()

        cache[normalizedQuery]?.let {
            emit(Result.Success(it))
            return@flow
        }

        safeApiCall {
            val response: List<BreedResponse> = apiService.searchBreeds(query)
            val breeds = response.toDomainModelList()
            cache[normalizedQuery] = breeds
            breeds
        }.collect { result ->
            emit(result)
        }
    }

    fun clearCache() {
        cache.clear()
    }

    fun clearCacheForQuery(query: String) {
        cache.remove(query.lowercase().trim())
    }

    fun isCached(query: String): Boolean {
        return cache.containsKey(query.lowercase().trim())
    }

    fun getCacheSize(): Int {
        return cache.size
    }
}