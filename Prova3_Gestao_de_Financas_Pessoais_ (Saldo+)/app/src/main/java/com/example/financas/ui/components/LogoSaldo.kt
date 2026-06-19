package com.example.financas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

val GoldColor = Color(0xFFD4AF37)

@Composable
fun LogoSaldo(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(200.dp)
    ) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 12f, cap = StrokeCap.Round)
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.22f

        // Círculo esquerdo do infinito
        drawCircle(
            color = GoldColor,
            radius = r,
            center = Offset(cx - r, cy),
            style = stroke
        )

        // Círculo direito do infinito
        drawCircle(
            color = GoldColor,
            radius = r,
            center = Offset(cx + r, cy),
            style = stroke
        )

        // "+" no centro direito
        val plusSize = r * 0.5f
        val plusX = cx + r
        val plusY = cy

        // Linha horizontal do "+"
        drawLine(
            color = GoldColor,
            start = Offset(plusX - plusSize, plusY),
            end = Offset(plusX + plusSize, plusY),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )

        // Linha vertical do "+"
        drawLine(
            color = GoldColor,
            start = Offset(plusX, plusY - plusSize),
            end = Offset(plusX, plusY + plusSize),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}