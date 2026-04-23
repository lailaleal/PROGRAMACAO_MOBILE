package com.example.prova1_api_catdog.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PetImage(
    val id: String? = null,
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)