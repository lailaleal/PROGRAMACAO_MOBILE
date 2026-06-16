package com.example.financas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.financas.model.Despesa

/**
 * ViewModel responsável por gerenciar o estado da tela de Gestão de Finanças Pessoais.
 *
 * Mantém a lista de despesas cadastradas, o orçamento (teto) definido pelo usuário,
 * e expõe valores calculados (total consumido e saldo restante) de forma reativa,
 * através do Compose State.
 */
class FinancasViewModel : ViewModel() {

    // Lista mutável de despesas. O Compose observa mudanças nessa lista automaticamente.
    var despesas = mutableStateOf<List<Despesa>>(emptyList())
        private set

    // Orçamento (teto) definido pelo usuário. Inicia em 0.0 (não definido).
    var orcamento by mutableStateOf(0.0)
        private set

    // Mensagens de erro de validação para os campos de entrada.
    var erroNomeDespesa by mutableStateOf<String?>(null)
        private set

    var erroValorDespesa by mutableStateOf<String?>(null)
        private set

    var erroOrcamento by mutableStateOf<String?>(null)
        private set

    /**
     * Soma de todas as despesas cadastradas.
     */
    val totalConsumido: Double
        get() = despesas.value.sumOf { it.valor }

    /**
     * Saldo restante = orçamento - total consumido.
     * Pode ser negativo, indicando que o usuário ultrapassou o teto.
     */
    val saldoRestante: Double
        get() = orcamento - totalConsumido

    /**
     * Percentual do orçamento já consumido (0.0 a 1.0+).
     * Retorna 0.0 se o orçamento ainda não foi definido (evita divisão por zero).
     */
    val percentualConsumido: Float
        get() = if (orcamento > 0) (totalConsumido / orcamento).toFloat() else 0f

    /**
     * Indica se o usuário ultrapassou o orçamento definido.
     */
    val orcamentoExcedido: Boolean
        get() = orcamento > 0 && totalConsumido > orcamento

    /**
     * Indica se o usuário está em estado de alerta (>= 80% do orçamento consumido),
     * mas ainda não excedeu.
     */
    val orcamentoEmAlerta: Boolean
        get() = orcamento > 0 && percentualConsumido >= 0.8f && !orcamentoExcedido

    /**
     * Define o orçamento mensal, validando o formato do valor informado.
     *
     * @param valorTexto valor digitado pelo usuário (como String).
     * @return true se o valor foi aceito e aplicado, false caso contrário.
     */
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

    /**
     * Adiciona uma nova despesa à lista, validando nome e valor.
     *
     * @param nome nome/descrição da despesa.
     * @param valorTexto valor da despesa (como String, vindo do campo de input).
     * @return true se a despesa foi adicionada com sucesso, false caso contrário.
     */
    fun adicionarDespesa(nome: String, valorTexto: String): Boolean {
        // Validação do nome
        erroNomeDespesa = when {
            nome.isBlank() -> "O nome da despesa não pode estar vazio."
            nome.length > 40 -> "O nome deve ter no máximo 40 caracteres."
            else -> null
        }

        // Validação do valor
        val valor = valorTexto.replace(",", ".").toDoubleOrNull()
        erroValorDespesa = when {
            valorTexto.isBlank() -> "Informe o valor da despesa."
            valor == null -> "Valor inválido. Use apenas números (ex: 49.90)."
            valor <= 0.0 -> "O valor deve ser maior que zero."
            else -> null
        }

        if (erroNomeDespesa == null && erroValorDespesa == null && valor != null) {
            val novaDespesa = Despesa(nome = nome.trim(), valor = valor)
            despesas.value = despesas.value + novaDespesa
            return true
        }
        return false
    }

    /**
     * Remove uma despesa da lista pelo seu id.
     */
    fun removerDespesa(id: String) {
        despesas.value = despesas.value.filterNot { it.id == id }
    }

    /**
     * Limpa as mensagens de erro de validação (útil ao abrir/fechar diálogos).
     */
    fun limparErros() {
        erroNomeDespesa = null
        erroValorDespesa = null
        erroOrcamento = null
    }
}