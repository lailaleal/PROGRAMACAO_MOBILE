package com.example.prova1_api_catdog.data.service

import com.example.prova1_api_catdog.data.dto.BreedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Endpoint para listar TODAS as raças
    @GET("v1/breeds")
    suspend fun getAllBreeds(): List<BreedResponse>

    // Endpoint para BUSCAR raça por nome (query parameter)
    @GET("v1/breeds/search")
    suspend fun searchBreeds(
        @Query("q") query: String
    ): List<BreedResponse>

    // Endpoint para buscar imagens por raça (opcional - complementar)
    @GET("v1/images/search")
    suspend fun searchImagesByBreed(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int = 10
    ): List<com.example.prova1_api_catdog.data.dto.PetImage>
}