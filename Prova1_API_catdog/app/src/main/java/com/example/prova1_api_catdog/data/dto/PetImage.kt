package com.example.prova1_api_catdog.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PetImage(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)