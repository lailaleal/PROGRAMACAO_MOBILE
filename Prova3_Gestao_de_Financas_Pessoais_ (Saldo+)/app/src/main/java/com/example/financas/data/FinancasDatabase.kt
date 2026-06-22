package com.example.financas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DespesaEntity::class, OrcamentoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FinancasDatabase : RoomDatabase() {

    abstract fun despesaDao(): DespesaDao

    companion object {
        @Volatile
        private var INSTANCE: FinancasDatabase? = null

        fun getDatabase(context: Context): FinancasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinancasDatabase::class.java,
                    "financas_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}