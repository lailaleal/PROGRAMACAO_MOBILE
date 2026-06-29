package com.laila.badalou.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// @Dao = Data Access Object
// Interface que define todas as operações no banco de dados
@Dao
interface TarefaDao {

    // Busca todas as tarefas ordenadas por horário
    // Flow = atualiza a tela automaticamente quando o banco muda
    @Query("SELECT * FROM tarefas ORDER BY horario ASC")
    fun buscarTodas(): Flow<List<Tarefa>>

    // Busca tarefas por categoria
    @Query("SELECT * FROM tarefas WHERE categoria = :categoria ORDER BY horario ASC")
    fun buscarPorCategoria(categoria: String): Flow<List<Tarefa>>

    // Busca uma tarefa específica pelo ID
    @Query("SELECT * FROM tarefas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Tarefa?

    // Insere uma nova tarefa no banco
    // OnConflictStrategy.REPLACE = se existir, substitui
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(tarefa: Tarefa): Long

    // Atualiza uma tarefa existente
    @Update
    suspend fun atualizar(tarefa: Tarefa)

    // Deleta uma tarefa
    @Delete
    suspend fun deletar(tarefa: Tarefa)

    // Marca uma tarefa como concluída ou não
    @Query("UPDATE tarefas SET concluida = :concluida WHERE id = :id")
    suspend fun atualizarConcluida(id: Int, concluida: Boolean)
}