package com.laila.badalou.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.laila.badalou.data.ThemePreferences
import com.laila.badalou.data.database.BadalouDatabase
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.data.repository.TarefaRepository
import com.laila.badalou.worker.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TarefaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TarefaRepository
    private val themePreferences: ThemePreferences

    val tarefas: StateFlow<List<Tarefa>>
    val isDarkMode: StateFlow<Boolean>

    private val _categoriaSelecionada = MutableStateFlow<String?>(null)
    val categoriaSelecionada: StateFlow<String?> = _categoriaSelecionada

    init {
        val dao = BadalouDatabase.getDatabase(application).tarefaDao()
        repository = TarefaRepository(dao)
        themePreferences = ThemePreferences(application)

        tarefas = repository.buscarTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        isDarkMode = themePreferences.isDarkMode
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    // Insere tarefa e agenda notificação exata
    fun inserirTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.inserir(tarefa)
            val lista = repository.buscarTodas()
            lista.collect { tarefas ->
                val tarefaInserida = tarefas.lastOrNull {
                    it.titulo == tarefa.titulo &&
                            it.horario == tarefa.horario
                }
                tarefaInserida?.let {
                    AlarmScheduler.agendarNotificacao(
                        context = getApplication(),
                        tarefaId = it.id,
                        titulo = it.titulo,
                        horario = it.horario
                    )
                }
                return@collect
            }
        }
    }

    // Atualiza tarefa e reagenda notificação
    fun atualizarTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.atualizar(tarefa)
            AlarmScheduler.cancelarNotificacao(
                context = getApplication(),
                tarefaId = tarefa.id
            )
            AlarmScheduler.agendarNotificacao(
                context = getApplication(),
                tarefaId = tarefa.id,
                titulo = tarefa.titulo,
                horario = tarefa.horario
            )
        }
    }

    // Deleta tarefa e cancela notificação
    fun deletarTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.deletar(tarefa)
            AlarmScheduler.cancelarNotificacao(
                context = getApplication(),
                tarefaId = tarefa.id
            )
        }
    }

    // Marca tarefa como concluída
    fun atualizarConcluida(id: Int, concluida: Boolean) {
        viewModelScope.launch {
            repository.atualizarConcluida(id, concluida)
        }
    }

    // Alterna entre tema claro e escuro
    fun toggleDarkMode() {
        viewModelScope.launch {
            themePreferences.saveDarkMode(!isDarkMode.value)
        }
    }

    // Filtra tarefas por categoria
    fun selecionarCategoria(categoria: String?) {
        _categoriaSelecionada.value = categoria
    }
}