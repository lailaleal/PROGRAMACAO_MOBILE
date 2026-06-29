package com.laila.badalou.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Tarefa::class],
    version = 1,
    exportSchema = false
)
abstract class BadalouDatabase : RoomDatabase() {

    abstract fun tarefaDao(): TarefaDao

    companion object {

        @Volatile
        private var INSTANCE: BadalouDatabase? = null

        fun getDatabase(context: Context): BadalouDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BadalouDatabase::class.java,
                    "badalou_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}