package com.example.financas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financas.model.Categoria
import com.example.financas.ui.components.GraficoPizza
import com.example.financas.viewmodel.FinancasViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFinancas(viewModel: FinancasViewModel = viewModel()) {

    var nomeDespesa by remember { mutableStateOf("") }
    var valorDespesa by remember { mutableStateOf("") }
    var valorOrcamento by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf(Categoria.OUTROS) }
    var despesaParaDeletar by remember { mutableStateOf<String?>(null) }
    var expandirCategoria by remember { mutableStateOf(false) }
    var mostrarDialogoLimpar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val goldColor = Color(0xFFD4AF37)

    if (despesaParaDeletar != null) {
        AlertDialog(
            onDismissRequest = { despesaParaDeletar = null },
            title = { Text("Remover despesa") },
            text = { Text("Tem certeza que deseja remover esta despesa?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removerDespesa(despesaParaDeletar!!)
                    despesaParaDeletar = null
                }) {
                    Text("Remover", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { despesaParaDeletar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoLimpar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLimpar = false },
            title = { Text("Limpar todas as despesas") },
            text = { Text("Tem certeza que deseja remover TODAS as despesas?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.limparTodas()
                    mostrarDialogoLimpar = false
                }) {
                    Text("Limpar tudo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLimpar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saldo+",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // ---------- CARD: ORÇAMENTO ----------
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Orçamento Mensal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = valorOrcamento,
                                onValueChange = { valorOrcamento = it },
                                label = { Text("Valor (R$)") },
                                isError = viewModel.erroOrcamento != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val sucesso = viewModel.definirOrcamento(valorOrcamento)
                                if (sucesso) valorOrcamento = ""
                            }) { Text("Definir") }
                        }
                        viewModel.erroOrcamento?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (viewModel.orcamento > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Orçamento: ${formatoMoeda.format(viewModel.orcamento)}",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val progresso = viewModel.percentualConsumido.coerceIn(0f, 1f)
                            val corBarra = when {
                                viewModel.orcamentoExcedido -> MaterialTheme.colorScheme.error
                                viewModel.orcamentoEmAlerta -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            }
                            Text(
                                text = "${(progresso * 100).toInt()}% utilizado",
                                style = MaterialTheme.typography.bodySmall,
                                color = corBarra
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progresso },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = corBarra,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // ---------- CARD: RESUMO ----------
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resumo do Mês",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
                                color = when {
                                    viewModel.orcamentoExcedido -> MaterialTheme.colorScheme.error
                                    viewModel.orcamentoEmAlerta -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }

                        if (viewModel.despesas.value.isNotEmpty()) {
                            val maiorDespesa = viewModel.despesas.value.maxByOrNull { it.valor }
                            maiorDespesa?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Maior gasto:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = it.categoria.icon,
                                            contentDescription = null,
                                            tint = goldColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = it.nome,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Nº de despesas:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${viewModel.despesas.value.size}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (viewModel.orcamentoExcedido) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠ Você ultrapassou o orçamento!",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (viewModel.orcamentoEmAlerta) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠ Mais de 80% do orçamento consumido.",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // ---------- CARD: GRÁFICO ----------
            if (viewModel.despesas.value.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            GraficoPizza(
                                despesas = viewModel.despesas.value,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // ---------- CARD: CADASTRO ----------
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Adicionar Despesa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = nomeDespesa,
                            onValueChange = { nomeDespesa = it },
                            label = { Text("Nome da despesa") },
                            isError = viewModel.erroNomeDespesa != null,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        viewModel.erroNomeDespesa?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
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
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        viewModel.erroValorDespesa?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandirCategoria,
                            onExpandedChange = { expandirCategoria = it }
                        ) {
                            OutlinedTextField(
                                value = categoriaSelecionada.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoria") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = categoriaSelecionada.icon,
                                        contentDescription = null,
                                        tint = goldColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandirCategoria
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandirCategoria,
                                onDismissRequest = { expandirCategoria = false }
                            ) {
                                Categoria.entries.forEach { cat ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = cat.icon,
                                                contentDescription = null,
                                                tint = goldColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        text = { Text(cat.label) },
                                        onClick = {
                                            categoriaSelecionada = cat
                                            expandirCategoria = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val sucesso = viewModel.adicionarDespesa(
                                    nomeDespesa,
                                    valorDespesa,
                                    categoriaSelecionada
                                )
                                if (sucesso) {
                                    nomeDespesa = ""
                                    valorDespesa = ""
                                    categoriaSelecionada = Categoria.OUTROS
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Adicionar Despesa") }
                    }
                }
            }

            // ---------- LISTA DE DESPESAS ----------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Despesas (${viewModel.despesas.value.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (viewModel.despesas.value.isNotEmpty()) {
                        TextButton(onClick = { mostrarDialogoLimpar = true }) {
                            Text(
                                text = "Limpar tudo",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            if (viewModel.despesas.value.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma despesa cadastrada ainda.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(viewModel.despesas.value) { despesa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = despesa.categoria.icon,
                                    contentDescription = null,
                                    tint = goldColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = despesa.nome,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = despesa.categoria.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = formatoMoeda.format(despesa.valor),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            IconButton(onClick = { despesaParaDeletar = despesa.id }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Remover",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}