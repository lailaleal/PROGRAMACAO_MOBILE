package com.laila.badalou.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkerScheduler {

    // Agenda a notificação para o horário definido
    fun agendarNotificacao(
        context: Context,
        tarefaId: Int,
        titulo: String,
        horario: String
    ) {
        // Calcula o delay até o horário programado
        val delay = calcularDelay(horario)

        // Se o horário já passou hoje, não agenda
        if (delay <= 0) return

        // Dados que serão enviados para o Worker
        val inputData = workDataOf(
            "titulo" to titulo,
            "mensagem" to "Badalou! Está na hora: $titulo",
            "notificationId" to tarefaId
        )

        // Cria a requisição do Worker
        val workRequest = OneTimeWorkRequestBuilder<TarefaWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("tarefa_$tarefaId")
            .build()

        // Agenda o Worker
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "tarefa_$tarefaId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    // Cancela a notificação de uma tarefa
    fun cancelarNotificacao(context: Context, tarefaId: Int) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("tarefa_$tarefaId")
    }

    // Calcula quantos milissegundos faltam para o horário
    private fun calcularDelay(horario: String): Long {
        val partes = horario.split(":")
        val hora = partes.getOrNull(0)?.toIntOrNull() ?: 0
        val minuto = partes.getOrNull(1)?.toIntOrNull() ?: 0

        val agora = Calendar.getInstance()
        val alvo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Se o horário já passou hoje, agenda para amanhã
        if (alvo.before(agora)) {
            alvo.add(Calendar.DAY_OF_MONTH, 1)
        }

        return alvo.timeInMillis - agora.timeInMillis
    }
}