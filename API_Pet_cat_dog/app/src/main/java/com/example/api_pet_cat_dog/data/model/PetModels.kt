package com.example.api_pet_cat_dog.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Breed(
    val id: String,
    val name: String,
    val description: String?,
    val temperament: String?,
    @Json(name = "origin") val origin: String?,
    @Json(name = "life_span") val lifeSpan: String?,
    @Json(name = "reference_image_id") val referenceImageId: String?
)

@JsonClass(generateAdapter = true)
data class PetImage(
    val id: String,
    val url: String,
    val breeds: List<Breed>?
)
