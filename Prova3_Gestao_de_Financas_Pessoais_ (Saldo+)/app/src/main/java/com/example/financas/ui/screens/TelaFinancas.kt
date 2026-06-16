package com.example.financas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financas.ui.theme.ErrorRed
import com.example.financas.ui.theme.SuccessGreen
import com.example.financas.ui.theme.WarningAmber
import com.example.financas.viewmodel.FinancasViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Tela principal do app de Gestão de Finanças Pessoais.
 *
 * Permite ao usuário:
 * - Definir um orçamento mensal (teto de gastos).
 * - Cadastrar despesas (nome e valor).
 * - Visualizar em tempo real o total consumido e o saldo restante.
 * - Receber alertas visuais quando o orçamento estiver próximo do limite ou excedido.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFinancas(viewModel: FinancasViewModel = viewModel()) {

    // Estados locais dos campos de input
    var nomeDespesa by remember { mutableStateOf("") }
    var valorDespesa by remember { mutableStateOf("") }
    var valorOrcamento by remember { mutableStateOf("") }

    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestão de Finanças Pessoais") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // ---------- CARD: ORÇAMENTO ----------
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Orçamento Mensal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = valorOrcamento,
                            onValueChange = { valorOrcamento = it },
                            label = { Text("Valor do orçamento (R$)") },
                            isError = viewModel.erroOrcamento != null,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val sucesso = viewModel.definirOrcamento(valorOrcamento)
                            if (sucesso) valorOrcamento = ""
                        }) {
                            Text("Definir")
                        }
                    }

                    viewModel.erroOrcamento?.let { erro ->
                        Text(
                            text = erro,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (viewModel.orcamento > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Orçamento definido: ${formatoMoeda.format(viewModel.orcamento)}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- CARD: RESUMO (TOTAL E SALDO) ----------
            val corSaldo = when {
                viewModel.orcamentoExcedido -> ErrorRed
                viewModel.orcamentoEmAlerta -> WarningAmber
                else -> SuccessGreen
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumo do Mês",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total consumido:")
                        Text(
                            text = formatoMoeda.format(viewModel.totalConsumido),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saldo restante:")
                        Text(
                            text = formatoMoeda.format(viewModel.saldoRestante),
                            fontWeight = FontWeight.Bold,
                            color = corSaldo
                        )
                    }

                    if (viewModel.orcamentoExcedido) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠ Atenção: você ultrapassou o orçamento mensal!",
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (viewModel.orcamentoEmAlerta) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠ Atenção: você já consumiu mais de 80% do orçamento.",
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- CARD: CADASTRO DE DESPESA ----------
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Adicionar Despesa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nomeDespesa,
                        onValueChange = { nomeDespesa = it },
                        label = { Text("Nome da despesa") },
                        isError = viewModel.erroNomeDespesa != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    viewModel.erroNomeDespesa?.let { erro ->
                        Text(
                            text = erro,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = valorDespesa,
                        onValueChange = { valorDespesa = it },
                        label = { Text("Valor (R$)") },
                        isError = viewModel.erroValorDespesa != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    viewModel.erroValorDespesa?.let { erro ->
                        Text(
                            text = erro,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val sucesso = viewModel.adicionarDespesa(nomeDespesa, valorDespesa)
                            if (sucesso) {
                                nomeDespesa = ""
                                valorDespesa = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Adicionar Despesa")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- LISTA DE DESPESAS ----------
            Text(
                text = "Despesas cadastradas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.despesas.value.isEmpty()) {
                Text(
                    text = "Nenhuma despesa cadastrada ainda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(viewModel.despesas.value) { despesa ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = despesa.nome,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = formatoMoeda.format(despesa.valor),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(onClick = { viewModel.removerDespesa(despesa.id) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Remover despesa",
                                        tint = ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}