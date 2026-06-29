package com.laila.badalou.data.repository

import com.laila.badalou.data.database.Tarefa
import com.laila.badalou.data.database.TarefaDao
import kotlinx.coroutines.flow.Flow

// Repository = ponte entre o ViewModel e o banco de dados
// O ViewModel não acessa o banco diretamente, sempre passa pelo Repository
class TarefaRepository(private val tarefaDao: TarefaDao) {

    // Busca todas as tarefas — retorna Flow para atualização automática
    fun buscarTodas(): Flow<List<Tarefa>> {
        return tarefaDao.buscarTodas()
    }

    // Busca tarefas por categoria
    fun buscarPorCategoria(categoria: String): Flow<List<Tarefa>> {
        return tarefaDao.buscarPorCategoria(categoria)
    }

    // Busca uma tarefa pelo ID
    suspend fun buscarPorId(id: Int): Tarefa? {
        return tarefaDao.buscarPorId(id)
    }

    // Insere uma nova tarefa
    suspend fun inserir(tarefa: Tarefa): Long {
        return tarefaDao.inserir(tarefa)
    }

    // Atualiza uma tarefa existente
    suspend fun atualizar(tarefa: Tarefa) {
        tarefaDao.atualizar(tarefa)
    }

    // Deleta uma tarefa
    suspend fun deletar(tarefa: Tarefa) {
        tarefaDao.deletar(tarefa)
    }

    // Marca tarefa como concluída ou não
    suspend fun atualizarConcluida(id: Int, concluida: Boolean) {
        tarefaDao.atualizarConcluida(id, concluida)
    }
}