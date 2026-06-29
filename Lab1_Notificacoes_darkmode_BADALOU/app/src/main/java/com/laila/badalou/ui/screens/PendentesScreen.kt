package com.laila.badalou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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

@Composable
fun PendentesScreen(
    tarefas: List<Tarefa>,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onEditTarefa: (Tarefa) -> Unit,
    onCheckedTarefa: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendentes = tarefas
        .filter { !it.concluida }
        .sortedWith(compareBy({ it.data }, { it.horario }))

    Column(modifier = modifier.fillMaxSize()) {

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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tarefas Pendentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${pendentes.size} tarefa(s) pendente(s)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (pendentes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "✅", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma tarefa pendente!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Você está em dia! 🎉",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 80.dp
                )
            ) {
                items(pendentes) { tarefa ->
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