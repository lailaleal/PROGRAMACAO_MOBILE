package com.example.prova1_api_catdog.domain.model

data class Breed(
    val id: String,
    val name: String,
    val description: String,
    val origin: String,
    val temperament: String,
    val imageUrl: String
)

