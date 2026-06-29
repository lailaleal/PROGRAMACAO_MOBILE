package com.laila.badalou.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarefas")
data class Tarefa(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descricao: String = "",
    val categoria: String,
    val horario: String,
    val data: String = "",  // formato "dd/MM/yyyy"
    val concluida: Boolean = false,
    val workerId: String = ""
)