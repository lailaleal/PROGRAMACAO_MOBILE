package com.laila.badalou

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.ui.components.BottomNavBar
import com.laila.badalou.ui.components.BottomNavItem
import com.laila.badalou.ui.screens.CalendarioScreen
import com.laila.badalou.ui.screens.ConcluidasScreen
import com.laila.badalou.ui.screens.EditTarefaScreen
import com.laila.badalou.ui.screens.HomeScreen
import com.laila.badalou.ui.screens.PendentesScreen
import com.laila.badalou.ui.screens.SplashScreen
import com.laila.badalou.ui.theme.BadalouTheme
import com.laila.badalou.ui.viewmodel.TarefaViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        solicitarPermissaoNotificacao()

        setContent {
            val viewModel: TarefaViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val tarefas by viewModel.tarefas.collectAsStateWithLifecycle()

            BadalouTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    var selectedRoute by remember { mutableStateOf(BottomNavItem.Hoje.route) }
                    var tarefaParaEditar by remember { mutableStateOf<Tarefa?>(null) }

                    when {
                        showSplash -> {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        }
                        tarefaParaEditar != null -> {
                            EditTarefaScreen(
                                tarefa = tarefaParaEditar!!,
                                onAtualizar = { tarefa ->
                                    viewModel.atualizarTarefa(tarefa)
                                    tarefaParaEditar = null
                                },
                                onDeletar = { tarefa ->
                                    viewModel.deletarTarefa(tarefa)
                                    tarefaParaEditar = null
                                },
                                onVoltar = { tarefaParaEditar = null }
                            )
                        }
                        else -> {
                            Scaffold(
                                bottomBar = {
                                    BottomNavBar(
                                        selectedRoute = selectedRoute,
                                        onItemSelected = { route ->
                                            selectedRoute = route
                                        }
                                    )
                                }
                            ) { innerPadding ->
                                when (selectedRoute) {
                                    BottomNavItem.Hoje.route -> {
                                        HomeScreen(
                                            tarefas = tarefas,
                                            isDarkMode = isDarkMode,
                                            onToggleTheme = { viewModel.toggleDarkMode() },
                                            onAddTarefa = {
                                                selectedRoute = BottomNavItem.Futuras.route
                                            },
                                            onEditTarefa = { tarefa ->
                                                tarefaParaEditar = tarefa
                                            },
                                            onCheckedTarefa = { id, concluida ->
                                                viewModel.atualizarConcluida(id, concluida)
                                            },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                    BottomNavItem.Futuras.route -> {
                                        CalendarioScreen(
                                            tarefas = tarefas,
                                            isDarkMode = isDarkMode,
                                            onToggleTheme = { viewModel.toggleDarkMode() },
                                            onSalvarTarefa = { tarefa ->
                                                viewModel.inserirTarefa(tarefa)
                                            },
                                            onEditTarefa = { tarefa ->
                                                tarefaParaEditar = tarefa
                                            },
                                            onCheckedTarefa = { id, concluida ->
                                                viewModel.atualizarConcluida(id, concluida)
                                            },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                    BottomNavItem.Pendentes.route -> {
                                        PendentesScreen(
                                            tarefas = tarefas,
                                            isDarkMode = isDarkMode,
                                            onToggleTheme = { viewModel.toggleDarkMode() },
                                            onEditTarefa = { tarefa ->
                                                tarefaParaEditar = tarefa
                                            },
                                            onCheckedTarefa = { id, concluida ->
                                                viewModel.atualizarConcluida(id, concluida)
                                            },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                    BottomNavItem.Concluidas.route -> {
                                        ConcluidasScreen(
                                            tarefas = tarefas,
                                            isDarkMode = isDarkMode,
                                            onToggleTheme = { viewModel.toggleDarkMode() },
                                            onEditTarefa = { tarefa ->
                                                tarefaParaEditar = tarefa
                                            },
                                            onCheckedTarefa = { id, concluida ->
                                                viewModel.atualizarConcluida(id, concluida)
                                            },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> { }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }
}