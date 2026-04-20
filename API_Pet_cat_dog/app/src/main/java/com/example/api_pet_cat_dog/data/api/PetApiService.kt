package com.example.api_pet_cat_dog.data.api

import com.example.api_pet_cat_dog.data.model.Breed
import com.example.api_pet_cat_dog.data.model.PetImage
import retrofit2.http.GET
import retrofit2.http.Query

interface PetApiService {
    @GET("breeds")
    suspend fun getBreeds(): List<Breed>

    @GET("breeds/search")
    suspend fun searchBreeds(@Query("q") query: String): List<Breed>

    @GET("images/search")
    suspend fun getImagesByBreed(@Query("breed_ids") breedId: String, @Query("limit") limit: Int = 1): List<PetImage>
}
