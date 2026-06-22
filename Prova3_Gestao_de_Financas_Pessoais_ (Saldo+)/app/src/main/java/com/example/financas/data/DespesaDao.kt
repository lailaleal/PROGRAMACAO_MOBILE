package com.example.financas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DespesaDao {

    // ---------- DESPESAS ----------
    @Query("SELECT * FROM despesas")
    fun buscarTodas(): Flow<List<DespesaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(despesa: DespesaEntity)

    @Query("DELETE FROM despesas WHERE id = :id")
    suspend fun deletar(id: String)

    @Query("DELETE FROM despesas")
    suspend fun deletarTodas()

    // ---------- ORÇAMENTO ----------
    @Query("SELECT * FROM orcamento WHERE id = 1")
    fun buscarOrcamento(): Flow<OrcamentoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarOrcamento(orcamento: OrcamentoEntity)

    @Query("DELETE FROM orcamento")
    suspend fun deletarOrcamento()
}