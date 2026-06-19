package com.example.financas.model

import java.util.UUID

enum class Categoria(val label: String, val emoji: String) {
    ALIMENTACAO("Alimentação", "🍔"),
    TRANSPORTE("Transporte", "🚗"),
    SAUDE("Saúde", "💊"),
    LAZER("Lazer", "🎮"),
    MORADIA("Moradia", "🏠"),
    EDUCACAO("Educação", "📚"),
    VESTUARIO("Vestuário", "👗"),
    OUTROS("Outros", "💳")
}

data class Despesa(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val valor: Double,
    val categoria: Categoria = Categoria.OUTROS
)