package com.example.financas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.financas.ui.screens.TelaFinancas
import com.example.financas.ui.theme.FinancasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancasTheme {
                TelaFinancas()
            }
        }
    }
}