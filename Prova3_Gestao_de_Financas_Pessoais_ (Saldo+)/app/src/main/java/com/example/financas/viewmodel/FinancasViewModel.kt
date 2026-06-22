package com.example.financas.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financas.data.DespesaEntity
import com.example.financas.data.FinancasDatabase
import com.example.financas.data.OrcamentoEntity
import com.example.financas.model.Categoria
import com.example.financas.model.Despesa
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FinancasViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FinancasDatabase.getDatabase(application).despesaDao()

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

    init {
        viewModelScope.launch {
            dao.buscarTodas().collectLatest { entities ->
                despesas.value = entities.map { entity ->
                    Despesa(
                        id = entity.id,
                        nome = entity.nome,
                        valor = entity.valor,
                        categoria = Categoria.valueOf(entity.categoria)
                    )
                }
            }
        }
        viewModelScope.launch {
            dao.buscarOrcamento().collectLatest { entity ->
                orcamento = entity?.valor ?: 0.0
            }
        }
    }

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
            viewModelScope.launch {
                dao.salvarOrcamento(OrcamentoEntity(valor = valor))
            }
            return true
        }
        return false
    }

    fun resetarOrcamento() {
        orcamento = 0.0
        viewModelScope.launch {
            dao.deletarOrcamento()
        }
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
            viewModelScope.launch {
                dao.inserir(
                    DespesaEntity(
                        id = novaDespesa.id,
                        nome = novaDespesa.nome,
                        valor = novaDespesa.valor,
                        categoria = novaDespesa.categoria.name
                    )
                )
            }
            return true
        }
        return false
    }

    fun removerDespesa(id: String) {
        viewModelScope.launch {
            dao.deletar(id)
        }
    }

    fun limparTodas() {
        viewModelScope.launch {
            dao.deletarTodas()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FinancasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FinancasViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}