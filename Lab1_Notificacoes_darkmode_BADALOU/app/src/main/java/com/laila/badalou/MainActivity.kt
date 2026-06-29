package com.laila.badalou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.laila.badalou.ui.screens.SplashScreen
import com.laila.badalou.ui.theme.BadalouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BadalouTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controla qual tela está sendo exibida
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        // Mostra a Splash Screen
                        SplashScreen(
                            onSplashFinished = {
                                showSplash = false
                            }
                        )
                    } else {
                        // Por enquanto mostra tela em branco
                        // Aqui virá a navegação principal nas próximas etapas
                    }
                }
            }
        }
    }
}