package com.example.financas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesas")
data class DespesaEntity(
    @PrimaryKey
    val id: String,
    val nome: String,
    val valor: Double,
    val categoria: String
)
