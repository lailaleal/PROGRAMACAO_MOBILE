package com.example.financas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financas.ui.screens.NavGraph
import com.example.financas.ui.theme.FinancasTheme
import com.example.financas.viewmodel.FinancasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancasTheme {
                val viewModel: FinancasViewModel = viewModel(
                    factory = FinancasViewModel.Factory(application)
                )
                NavGraph(viewModel = viewModel)
            }
        }
    }
}