package com.laila.badalou.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.laila.badalou.data.ThemePreferences
import com.laila.badalou.data.database.BadalouDatabase
import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.data.repository.TarefaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel = ViewModel que tem acesso ao contexto do app
class TarefaViewModel(application: Application) : AndroidViewModel(application) {

    // Instância do repositório
    private val repository: TarefaRepository

    // Instância do DataStore para o tema
    private val themePreferences: ThemePreferences

    // Lista de tarefas exposta para a UI
    val tarefas: StateFlow<List<Tarefa>>

    // Estado do tema — false = claro, true = escuro
    val isDarkMode: StateFlow<Boolean>

    // Categoria selecionada para filtrar tarefas
    private val _categoriaSelecionada = MutableStateFlow<String?>(null)
    val categoriaSelecionada: StateFlow<String?> = _categoriaSelecionada

    init {
        // Inicializa o banco de dados e repositório
        val dao = BadalouDatabase.getDatabase(application).tarefaDao()
        repository = TarefaRepository(dao)

        // Inicializa o DataStore
        themePreferences = ThemePreferences(application)

        // Converte o Flow de tarefas em StateFlow
        tarefas = repository.buscarTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Converte o Flow do tema em StateFlow
        isDarkMode = themePreferences.isDarkMode
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    // Insere uma nova tarefa
    fun inserirTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.inserir(tarefa)
        }
    }

    // Atualiza uma tarefa existente
    fun atualizarTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.atualizar(tarefa)
        }
    }

    // Deleta uma tarefa
    fun deletarTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.deletar(tarefa)
        }
    }

    // Marca tarefa como concluída ou não
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