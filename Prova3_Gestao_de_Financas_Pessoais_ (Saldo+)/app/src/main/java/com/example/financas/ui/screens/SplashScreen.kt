package com.example.financas.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.financas.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    var iniciarAnimacao by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (iniciarAnimacao) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        iniciarAnimacao = true
        delay(2500)
        onSplashFinished()
    }

    Image(
        painter = painterResource(id = R.drawable.splash_background),
        contentDescription = "Splash Saldo+",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .border(2.dp, Color(0xFFD4AF37))
    )
}