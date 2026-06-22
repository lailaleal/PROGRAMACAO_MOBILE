package com.example.financas.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

enum class Categoria(val label: String, val icon: ImageVector) {
    ALIMENTACAO("Alimentação", Icons.Filled.Restaurant),
    TRANSPORTE("Transporte", Icons.Filled.DirectionsCar),
    SAUDE("Saúde", Icons.Filled.LocalHospital),
    LAZER("Lazer", Icons.Filled.SportsEsports),
    MORADIA("Moradia", Icons.Filled.Home),
    EDUCACAO("Educação", Icons.Filled.MenuBook),
    VESTUARIO("Vestuário", Icons.Filled.Checkroom),
    OUTROS("Outros", Icons.Filled.AccountBalanceWallet)
}

data class Despesa(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val valor: Double,
    val categoria: Categoria = Categoria.OUTROS
)