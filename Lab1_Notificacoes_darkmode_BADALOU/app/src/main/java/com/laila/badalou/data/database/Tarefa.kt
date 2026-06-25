package com.laila.badalou.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity diz ao Room que esta classe é uma tabela no banco de dados
// tableName define o nome da tabela
@Entity(tableName = "tarefas")
data class Tarefa(

    // @PrimaryKey = chave única de cada tarefa
    // autoGenerate = o banco gera o ID automaticamente (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Título da tarefa (ex: "Beber água")
    val titulo: String,

    // Descrição opcional da tarefa
    val descricao: String = "",

    // Categoria (ex: "Estudar", "Exercício", "Medicamento")
    val categoria: String,

    // Horário do lembrete no formato "HH:mm" (ex: "08:30")
    val horario: String,

    // Data no formato "dd/MM/yyyy" (ex: "25/06/2026")
    val data: String = "",

    // true = tarefa concluída, false = tarefa pendente
    val concluida: Boolean = false,

    // ID do WorkManager para poder cancelar a notificação
    val workerId: String = ""
)