package com.laila.badalou.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.ui.theme.*

@Composable
fun TaskCard(
    tarefa: Tarefa,
    onChecked: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val categoriaColor = getCategoriaColor(tarefa.categoria)
    val categoriaIcon = getCategoriaIcon(tarefa.categoria)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra lateral colorida da categoria
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(categoriaColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Informações da tarefa
            Column(modifier = Modifier.weight(1f)) {

                // Chip da categoria com ícone âmbar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = categoriaColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = categoriaIcon,
                        contentDescription = tarefa.categoria,
                        tint = AmbarPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tarefa.categoria,
                        fontSize = 11.sp,
                        color = categoriaColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Título da tarefa
                Text(
                    text = tarefa.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (tarefa.concluida)
                        TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Horário
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Horário",
                        tint = AmbarPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tarefa.horario,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Checkbox de conclusão
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (tarefa.concluida) GreenSuccess
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (tarefa.concluida) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluída",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Checkbox(
                    checked = tarefa.concluida,
                    onCheckedChange = onChecked,
                    modifier = Modifier.size(32.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Transparent,
                        uncheckedColor = Color.Transparent,
                        checkmarkColor = Color.Transparent
                    )
                )
            }
        }
    }
}

// Retorna a cor de cada categoria
fun getCategoriaColor(categoria: String): Color {
    return when (categoria) {
        "Estudar" -> CategoriaEstudar
        "Beber Água" -> CategoriaBeberAgua
        "Exercício" -> CategoriaExercicio
        "Medicamento" -> CategoriaMedicamento
        "Compromisso" -> CategoriaCompromisso
        else -> CategoriaOutros
    }
}

// Retorna o ícone de cada categoria
fun getCategoriaIcon(categoria: String): ImageVector {
    return when (categoria) {
        "Estudar" -> Icons.Default.MenuBook
        "Beber Água" -> Icons.Default.WaterDrop
        "Exercício" -> Icons.Default.FitnessCenter
        "Medicamento" -> Icons.Default.Medication
        "Compromisso" -> Icons.Default.CalendarMonth
        else -> Icons.Default.PushPin
    }
}