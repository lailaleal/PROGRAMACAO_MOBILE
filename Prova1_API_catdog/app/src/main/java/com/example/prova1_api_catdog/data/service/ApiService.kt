package com.example.prova1_api_catdog.data.service

import com.example.prova1_api_catdog.data.dto.BreedResponse
import com.example.prova1_api_catdog.data.dto.PetImage
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Listar todas as raças
    @GET("v1/breeds")
    suspend fun getAllBreeds(): List<BreedResponse>

    // Buscar raça por NOME (funciona para TheDogAPI)
    @GET("v1/breeds/search")
    suspend fun searchBreedsByName(
        @Query("q") query: String
    ): List<BreedResponse>

    // Buscar imagens por ID da raça (funciona para TheCatAPI)
    @GET("v1/images/search")
    suspend fun searchImagesByBreedId(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int = 10
    ): List<PetImage>

    // Buscar imagens aleatórias (para a tela inicial)
    @GET("v1/images/search")
    suspend fun getRandomImages(
        @Query("limit") limit: Int = 2
    ): List<PetImage>
}