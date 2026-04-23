package com.example.prova1_api_catdog.data.dto

import com.example.prova1_api_catdog.domain.model.Breed

fun BreedResponse.toDomainModel(): Breed {
    return Breed(
        id = this.id,
        name = this.name,
        description = this.description ?: "Sem descrição",
        origin = this.origin ?: "Origem desconhecida",
        temperament = this.temperament ?: "Sem informação",
        imageUrl = this.image?.url ?: ""
    )
}

fun List<BreedResponse>.toDomainModelList(): List<Breed> {
    return this.map { it.toDomainModel() }
}