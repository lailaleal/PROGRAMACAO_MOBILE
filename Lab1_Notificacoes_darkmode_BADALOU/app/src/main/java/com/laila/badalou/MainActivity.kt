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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.ui.screens.AddTarefaScreen
import com.laila.badalou.ui.screens.EditTarefaScreen
import com.laila.badalou.ui.screens.HomeScreen
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
                    var showAddTarefa by remember { mutableStateOf(false) }
                    var tarefaParaEditar by remember { mutableStateOf<Tarefa?>(null) }

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
                            HomeScreen(
                                tarefas = tarefas,
                                isDarkMode = isDarkMode,
                                onToggleTheme = { viewModel.toggleDarkMode() },
                                onAddTarefa = { showAddTarefa = true },
                                onEditTarefa = { tarefa ->
                                    tarefaParaEditar = tarefa
                                },
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