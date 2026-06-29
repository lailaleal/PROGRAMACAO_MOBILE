package com.laila.badalou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.ui.components.TaskCard
import com.laila.badalou.ui.theme.AmbarPrimary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    tarefas: List<Tarefa>,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSalvarTarefa: (Tarefa) -> Unit,
    onEditTarefa: (Tarefa) -> Unit,
    onCheckedTarefa: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    var mesSelecionado by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var anoSelecionado by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var diaSelecionado by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var showAddTarefa by remember { mutableStateOf(false) }

    val meses = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril",
        "Maio", "Junho", "Julho", "Agosto",
        "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    val dataSelecionada = "%02d/%02d/%04d".format(
        diaSelecionado, mesSelecionado + 1, anoSelecionado
    )

    val tarefasDoDia = tarefas
        .filter { it.data == dataSelecionada }
        .sortedBy { it.horario }

    val diasComTarefas = tarefas
        .filter {
            val partes = it.data.split("/")
            if (partes.size == 3) {
                partes[1].toIntOrNull() == mesSelecionado + 1 &&
                        partes[2].toIntOrNull() == anoSelecionado
            } else false
        }
        .map { it.data.split("/")[0].toIntOrNull() ?: 0 }
        .toSet()

    val calMes = Calendar.getInstance().apply {
        set(Calendar.YEAR, anoSelecionado)
        set(Calendar.MONTH, mesSelecionado)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val primeiroDiaSemana = calMes.get(Calendar.DAY_OF_WEEK) - 1
    val totalDias = calMes.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Dia selecionado é passado?
    val diaSelecionadoIsPast = run {
        val hoje = Calendar.getInstance()
        when {
            anoSelecionado < hoje.get(Calendar.YEAR) -> true
            anoSelecionado == hoje.get(Calendar.YEAR) &&
                    mesSelecionado < hoje.get(Calendar.MONTH) -> true
            anoSelecionado == hoje.get(Calendar.YEAR) &&
                    mesSelecionado == hoje.get(Calendar.MONTH) &&
                    diaSelecionado < hoje.get(Calendar.DAY_OF_MONTH) -> true
            else -> false
        }
    }

    if (showAddTarefa) {
        AddTarefaScreen(
            dataInicial = dataSelecionada,
            onSalvar = { tarefa ->
                onSalvarTarefa(tarefa)
                showAddTarefa = false
            },
            onVoltar = { showAddTarefa = false }
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {

        // ── HEADER ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmbarPrimary)
                .padding(horizontal = 20.dp, vertical = 16.dp)
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Calendário",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (mesSelecionado == 0) {
                                    mesSelecionado = 11
                                    anoSelecionado--
                                } else {
                                    mesSelecionado--
                                }
                            }) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Mês anterior",
                                    tint = AmbarPrimary
                                )
                            }

                            Text(
                                text = "${meses[mesSelecionado]} $anoSelecionado",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmbarPrimary
                            )

                            IconButton(onClick = {
                                if (mesSelecionado == 11) {
                                    mesSelecionado = 0
                                    anoSelecionado++
                                } else {
                                    mesSelecionado++
                                }
                            }) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Próximo mês",
                                    tint = AmbarPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            diasSemana.forEach { dia ->
                                Text(
                                    text = dia,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val totalCelulas = primeiroDiaSemana + totalDias
                        val linhas = (totalCelulas + 6) / 7

                        for (linha in 0 until linhas) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (col in 0..6) {
                                    val index = linha * 7 + col
                                    val dia = index - primeiroDiaSemana + 1

                                    if (dia < 1 || dia > totalDias) {
                                        Box(modifier = Modifier.weight(1f))
                                    } else {
                                        val hoje = calendar
                                        val isHoje = dia == hoje.get(Calendar.DAY_OF_MONTH) &&
                                                mesSelecionado == hoje.get(Calendar.MONTH) &&
                                                anoSelecionado == hoje.get(Calendar.YEAR)
                                        val isSelecionado = dia == diaSelecionado
                                        val temTarefa = dia in diasComTarefas

                                        // Verifica se a data já passou
                                        val isPast = when {
                                            anoSelecionado < hoje.get(Calendar.YEAR) -> true
                                            anoSelecionado == hoje.get(Calendar.YEAR) &&
                                                    mesSelecionado < hoje.get(Calendar.MONTH) -> true
                                            anoSelecionado == hoje.get(Calendar.YEAR) &&
                                                    mesSelecionado == hoje.get(Calendar.MONTH) &&
                                                    dia < hoje.get(Calendar.DAY_OF_MONTH) -> true
                                            else -> false
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelecionado && !isPast -> AmbarPrimary
                                                        isHoje -> AmbarPrimary.copy(alpha = 0.2f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable(enabled = !isPast) {
                                                    diaSelecionado = dia
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = dia.toString(),
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isHoje || isSelecionado)
                                                        FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        isPast -> MaterialTheme.colorScheme
                                                            .onSurface.copy(alpha = 0.3f)
                                                        isSelecionado -> Color.White
                                                        isHoje -> AmbarPrimary
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    },
                                                    modifier = Modifier.padding(6.dp)
                                                )
                                                if (temTarefa) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(
                                                                if (isSelecionado)
                                                                    Color.White
                                                                else
                                                                    AmbarPrimary,
                                                                CircleShape
                                                            )
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
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$diaSelecionado de ${meses[mesSelecionado]}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Botão desabilitado para datas passadas
                    Button(
                        onClick = { showAddTarefa = true },
                        enabled = !diaSelecionadoIsPast,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmbarPrimary,
                            disabledContainerColor = AmbarPrimary.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Adicionar",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (tarefasDoDia.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (diaSelecionadoIsPast)
                                "Data passada — não é possível adicionar tarefas"
                            else
                                "Nenhuma tarefa neste dia",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(tarefasDoDia) { tarefa ->
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