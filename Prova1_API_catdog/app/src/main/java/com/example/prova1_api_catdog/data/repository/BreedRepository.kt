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

        println("🔍 Buscando: $query para o tipo: $petType")

        cache[cacheKey]?.let {
            println("📦 Retornando do cache: $cacheKey")
            emit(Result.Success(it))
            return@flow
        }

        val apiService = if (petType == "cat") catApiService else dogApiService
        val imageHost = if (petType == "cat") "thecatapi.com" else "thedogapi.com"

        safeApiCall {
            val response: List<BreedResponse> = apiService.searchBreedsByName(query)
            println("🌐 API Respondeu com ${response.size} raças")

            val breedsWithImages = response.map { breedResponse ->
                val imageUrl = when {
                    breedResponse.image?.url != null -> breedResponse.image.url
                    breedResponse.reference_image_id != null -> {
                        "https://cdn2.$imageHost/images/${breedResponse.reference_image_id}.jpg"
                    }
                    else -> ""
                }
                breedResponse.toDomainModel().copy(imageUrl = imageUrl)
            }

            cache[cacheKey] = breedsWithImages
            breedsWithImages
        }.collect { result ->
            emit(result)
        }
    }

    fun getRandomPhotos(): Flow<Result<List<String>>> = flow {
        emit(Result.Loading)
        try {
            // Buscamos fotos de gatos e cães de forma segura (se um falhar, o outro continua)
            val cats = try { catApiService.getRandomImages(2).mapNotNull { it.url } } catch (e: Exception) { emptyList() }
            val dogs = try { dogApiService.getRandomImages(2).mapNotNull { it.url } } catch (e: Exception) { emptyList() }
            
            val allPhotos = (cats + dogs).shuffled()
            println("📸 Home: Carregadas ${allPhotos.size} fotos aleatórias")
            emit(Result.Success(allPhotos))
        } catch (e: Exception) {
            println("❌ Erro ao carregar fotos da home: ${e.message}")
            emit(Result.Error(e))
        }
    }

    fun clearCache() {
        cache.clear()
    }
}