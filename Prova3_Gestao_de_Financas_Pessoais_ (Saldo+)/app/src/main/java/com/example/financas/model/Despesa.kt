package com.example.financas.model

import java.util.UUID

/**
 * Representa uma despesa cadastrada pelo usuário.
 *
 * @param id identificador único da despesa (gerado automaticamente).
 * @param nome descrição/nome da despesa (ex: "Aluguel", "Mercado").
 * @param valor valor monetário da despesa, sempre positivo.
 */
data class Despesa(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val valor: Double
)