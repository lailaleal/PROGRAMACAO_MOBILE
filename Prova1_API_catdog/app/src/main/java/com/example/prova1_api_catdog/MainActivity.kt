package com.example.prova1_api_catdog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prova1_api_catdog.presentation.screens.SearchScreen
import com.example.prova1_api_catdog.ui.theme.Prova1_API_catdogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prova1_API_catdogTheme {
                SearchScreen()
            }
        }
    }
}