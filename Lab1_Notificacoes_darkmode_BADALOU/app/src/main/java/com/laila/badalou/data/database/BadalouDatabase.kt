package com.laila.badalou.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database define esta classe como o banco de dados principal
// entities = lista de tabelas do banco
// version = versão do banco (incrementar quando mudar a estrutura)
@Database(
    entities = [Tarefa::class],
    version = 1,
    exportSchema = false
)
abstract class BadalouDatabase : RoomDatabase() {

    // Referência para o DAO de tarefas
    abstract fun tarefaDao(): TarefaDao

    // Companion object = permite acessar sem instanciar a classe
    companion object {

        // @Volatile = garante que todas as threads vejam o mesmo valor
        @Volatile
        private var INSTANCE: BadalouDatabase? = null

        // Função que retorna a instância única do banco (Singleton)
        fun getDatabase(context: Context): BadalouDatabase {

            // Se já existe uma instância, retorna ela
            return INSTANCE ?: synchronized(this) {

                // Cria o banco de dados
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BadalouDatabase::class.java,
                    "badalou_database" // nome do arquivo do banco
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}