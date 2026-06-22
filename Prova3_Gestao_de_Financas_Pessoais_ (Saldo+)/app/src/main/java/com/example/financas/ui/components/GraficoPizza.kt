package com.example.financas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financas.model.Categoria
import com.example.financas.model.Despesa

val coresCategorias = mapOf(
    Categoria.ALIMENTACAO to Color(0xFFD4AF37),
    Categoria.TRANSPORTE  to Color(0xFFB8932C),
    Categoria.SAUDE       to Color(0xFFE8C766),
    Categoria.LAZER       to Color(0xFFC8A951),
    Categoria.MORADIA     to Color(0xFF8B6914),
    Categoria.EDUCACAO    to Color(0xFFD4964A),
    Categoria.VESTUARIO   to Color(0xFFEDD99A),
    Categoria.OUTROS      to Color(0xFF6B5B2E)
)

@Composable
fun GraficoPizza(
    despesas: List<Despesa>,
    modifier: Modifier = Modifier
) {
    if (despesas.isEmpty()) return

    val totalPorCategoria = despesas
        .groupBy { it.categoria }
        .mapValues { entry -> entry.value.sumOf { it.valor } }

    val total = totalPorCategoria.values.sum()
    if (total == 0.0) return

    Column(modifier = modifier) {
        Text(
            text = "Gastos por Categoria",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(150.dp)) {
                var startAngle = -90f
                totalPorCategoria.forEach { (categoria, valor) ->
                    val sweepAngle = (valor / total * 360).toFloat()
                    val cor = coresCategorias[categoria] ?: Color.Gray
                    drawArc(
                        color = cor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                totalPorCategoria.forEach { (categoria, valor) ->
                    val cor = coresCategorias[categoria] ?: Color.Gray
                    val percentual = (valor / total * 100).toInt()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCircle(color = cor)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = categoria.icon,
                            contentDescription = categoria.label,
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${categoria.label} $percentual%",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}