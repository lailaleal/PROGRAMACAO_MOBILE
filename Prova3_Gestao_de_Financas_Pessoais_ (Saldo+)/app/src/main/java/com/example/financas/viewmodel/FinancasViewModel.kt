package com.example.financas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.financas.model.Categoria
import com.example.financas.model.Despesa

class FinancasViewModel : ViewModel() {

    var despesas = mutableStateOf<List<Despesa>>(emptyList())
        private set

    var orcamento by mutableStateOf(0.0)
        private set

    var erroNomeDespesa by mutableStateOf<String?>(null)
        private set

    var erroValorDespesa by mutableStateOf<String?>(null)
        private set

    var erroOrcamento by mutableStateOf<String?>(null)
        private set

    val totalConsumido: Double
        get() = despesas.value.sumOf { it.valor }

    val saldoRestante: Double
        get() = orcamento - totalConsumido

    val percentualConsumido: Float
        get() = if (orcamento > 0) (totalConsumido / orcamento).toFloat() else 0f

    val orcamentoExcedido: Boolean
        get() = orcamento > 0 && totalConsumido > orcamento

    val orcamentoEmAlerta: Boolean
        get() = orcamento > 0 && percentualConsumido >= 0.8f && !orcamentoExcedido

    fun definirOrcamento(valorTexto: String): Boolean {
        val valor = valorTexto.replace(",", ".").toDoubleOrNull()
        erroOrcamento = when {
            valorTexto.isBlank() -> "Informe um valor para o orçamento."
            valor == null -> "Valor inválido. Use apenas números (ex: 1500.00)."
            valor <= 0.0 -> "O orçamento deve ser maior que zero."
            else -> null
        }
        if (erroOrcamento == null && valor != null) {
            orcamento = valor
            return true
        }
        return false
    }

    fun adicionarDespesa(
        nome: String,
        valorTexto: String,
        categoria: Categoria = Categoria.OUTROS
    ): Boolean {
        erroNomeDespesa = when {
            nome.isBlank() -> "O nome da despesa não pode estar vazio."
            nome.length > 40 -> "O nome deve ter no máximo 40 caracteres."
            else -> null
        }
        val valor = valorTexto.replace(",", ".").toDoubleOrNull()
        erroValorDespesa = when {
            valorTexto.isBlank() -> "Informe o valor da despesa."
            valor == null -> "Valor inválido. Use apenas números (ex: 49.90)."
            valor <= 0.0 -> "O valor deve ser maior que zero."
            else -> null
        }
        if (erroNomeDespesa == null && erroValorDespesa == null && valor != null) {
            val novaDespesa = Despesa(
                nome = nome.trim(),
                valor = valor,
                categoria = categoria
            )
            despesas.value = despesas.value + novaDespesa
            return true
        }
        return false
    }

    fun removerDespesa(id: String) {
        despesas.value = despesas.value.filterNot { it.id == id }
    }

    fun limparTodas() {
        despesas.value = emptyList()
    }
}