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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laila.badalou.ui.screens.AddTarefaScreen
import com.laila.badalou.ui.screens.HomeScreen
import com.laila.badalou.ui.screens.SplashScreen
import com.laila.badalou.ui.theme.BadalouTheme
import com.laila.badalou.ui.viewmodel.TarefaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TarefaViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val tarefas by viewModel.tarefas.collectAsStateWithLifecycle()

            BadalouTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controle de navegação simples
                    var showSplash by remember { mutableStateOf(true) }
                    var showAddTarefa by remember { mutableStateOf(false) }

                    when {
                        showSplash -> {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        }
                        showAddTarefa -> {
                            AddTarefaScreen(
                                onSalvar = { tarefa ->
                                    viewModel.inserirTarefa(tarefa)
                                    showAddTarefa = false
                                },
                                onVoltar = { showAddTarefa = false }
                            )
                        }
                        else -> {
                            HomeScreen(
                                tarefas = tarefas,
                                isDarkMode = isDarkMode,
                                onToggleTheme = { viewModel.toggleDarkMode() },
                                onAddTarefa = { showAddTarefa = true },
                                onEditTarefa = { /* Etapa 8 */ },
                                onCheckedTarefa = { id, concluida ->
                                    viewModel.atualizarConcluida(id, concluida)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}