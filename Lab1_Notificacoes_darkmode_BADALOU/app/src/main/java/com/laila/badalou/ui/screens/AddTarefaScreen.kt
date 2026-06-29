package com.laila.badalou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.ui.components.getCategoriaColor
import com.laila.badalou.ui.components.getCategoriaIcon
import com.laila.badalou.ui.theme.AmbarPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTarefaScreen(
    dataInicial: String = "",
    onSalvar: (Tarefa) -> Unit,
    onVoltar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("Estudar") }
    var horario by remember { mutableStateOf("08:00") }
    var data by remember { mutableStateOf(dataInicial) }
    var showTimePicker by remember { mutableStateOf(false) }
    var hora by remember { mutableStateOf(8) }
    var minuto by remember { mutableStateOf(0) }

    val categorias = listOf(
        "Estudar", "Beber Água", "Exercício",
        "Medicamento", "Compromisso"
    )

    val formValido = titulo.isNotBlank()

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hora,
            initialMinute = minuto
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hora = timePickerState.hour
                    minuto = timePickerState.minute
                    horario = "%02d:%02d".format(hora, minuto)
                    showTimePicker = false
                }) {
                    Text("OK", color = AmbarPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Escolha o horário") },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectorColor = AmbarPrimary,
                        timeSelectorSelectedContainerColor = AmbarPrimary,
                        timeSelectorSelectedContentColor = Color.White
                    )
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nova Tarefa",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    Text(
                        text = "BADALOU",
                        color = AmbarPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── DATA SELECIONADA ─────────────────────
            if (dataInicial.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AmbarPrimary.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = AmbarPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Data: $dataInicial",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AmbarPrimary
                        )
                    }
                }
            }

            // ── CAMPO TÍTULO ─────────────────────────
            Text(
                text = "O que você vai realizar?",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text("Nome da tarefa") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmbarPrimary,
                    cursorColor = AmbarPrimary
                ),
                singleLine = true
            )

            // ── CAMPO DESCRIÇÃO ──────────────────────
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmbarPrimary,
                    cursorColor = AmbarPrimary
                ),
                minLines = 2
            )

            // ── CATEGORIAS ───────────────────────────
            Text(
                text = "Escolha uma categoria",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.chunked(2).forEach { linha ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        linha.forEach { categoria ->
                            val selecionada = categoria == categoriaSelecionada
                            val icone = getCategoriaIcon(categoria)

                            Card(
                                onClick = { categoriaSelecionada = categoria },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selecionada)
                                        AmbarPrimary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = if (selecionada) null else
                                    androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = icone,
                                        contentDescription = categoria,
                                        tint = if (selecionada) Color.White else AmbarPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = categoria,
                                        fontSize = 13.sp,
                                        fontWeight = if (selecionada)
                                            FontWeight.Bold else FontWeight.Normal,
                                        color = if (selecionada)
                                            Color.White
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        if (linha.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── HORÁRIO ──────────────────────────────
            Text(
                text = "Qual o melhor horário?",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                onClick = { showTimePicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = horario,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmbarPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Horário",
                        tint = AmbarPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // ── BOTÃO SALVAR ─────────────────────────
            Button(
                onClick = {
                    if (formValido) {
                        onSalvar(
                            Tarefa(
                                titulo = titulo,
                                descricao = descricao,
                                categoria = categoriaSelecionada,
                                horario = horario,
                                data = data
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmbarPrimary
                ),
                enabled = formValido
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SALVAR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}