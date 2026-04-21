package com.example.prova1_api_catdog.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BreedResponse(
    val id: String,
    val name: String,
    val description: String? = "",
    val origin: String? = "",
    val temperament: String? = "",
    val reference_image_id: String? = null
)
