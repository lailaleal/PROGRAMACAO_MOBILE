package com.laila.badalou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.ui.components.TaskCard
import com.laila.badalou.ui.theme.AmbarPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tarefas: List<Tarefa>,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onAddTarefa: () -> Unit,
    onEditTarefa: (Tarefa) -> Unit,
    onCheckedTarefa: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val categorias = listOf("Todas", "Estudar", "Beber Água", "Exercício", "Medicamento", "Compromisso")
    var categoriaSelecionada by remember { mutableStateOf("Todas") }

    val tarefasFiltradas = if (categoriaSelecionada == "Todas") {
        tarefas
    } else {
        tarefas.filter { it.categoria == categoriaSelecionada }
    }

    val concluidas = tarefas.count { it.concluida }
    val total = tarefas.size

    val calendar = java.util.Calendar.getInstance()
    val diasSemana = arrayOf(
        "Domingo", "Segunda", "Terça",
        "Quarta", "Quinta", "Sexta", "Sábado"
    )
    val meses = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril",
        "Maio", "Junho", "Julho", "Agosto",
        "Setembro", "Outubro", "Novembro", "Dezembro"
    )
    val diaSemana = diasSemana[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
    val dia = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val mes = meses[calendar.get(java.util.Calendar.MONTH)]
    val dataFormatada = "$dia de $mes"

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTarefa,
                containerColor = AmbarPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar tarefa",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── HEADER ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AmbarPrimary)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔔 BADALOU",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isDarkMode)
                                    Icons.Default.LightMode
                                else
                                    Icons.Default.DarkMode,
                                contentDescription = "Alternar tema",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$diaSemana, $dataFormatada",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$concluidas/$total",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Tasks Done",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔥",
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Foco!",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // ── FILTRO DE CATEGORIAS ─────────────────
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    val selecionada = categoria == categoriaSelecionada
                    FilterChip(
                        selected = selecionada,
                        onClick = { categoriaSelecionada = categoria },
                        label = {
                            Text(
                                text = categoria,
                                fontWeight = if (selecionada)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmbarPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // ── LISTA DE TAREFAS ─────────────────────
            if (tarefasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma tarefa ainda!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Toque no + para adicionar",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tarefasFiltradas) { tarefa ->
                        TaskCard(
                            tarefa = tarefa,
                            onChecked = { concluida ->
                                onCheckedTarefa(tarefa.id, concluida)
                            },
                            onClick = { onEditTarefa(tarefa) }
                        )
                    }
                }
            }
        }
    }
}