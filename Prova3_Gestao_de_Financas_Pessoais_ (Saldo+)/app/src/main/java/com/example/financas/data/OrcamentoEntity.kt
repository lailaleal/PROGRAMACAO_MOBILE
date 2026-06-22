package com.example.financas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orcamento")
data class OrcamentoEntity(
    @PrimaryKey
    val id: Int = 1,
    val valor: Double
)