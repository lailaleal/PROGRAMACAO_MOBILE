package com.laila.badalou.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TarefaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Recupera os dados passados ao agendar a notificação
        val titulo = inputData.getString("titulo") ?: "Badalou"
        val mensagem = inputData.getString("mensagem") ?: "Está na hora da sua tarefa!"
        val notificationId = inputData.getInt("notificationId", 0)

        // Cria o canal e exibe a notificação
        NotificationHelper.createNotificationChannel(applicationContext)
        NotificationHelper.showNotification(
            context = applicationContext,
            titulo = titulo,
            mensagem = mensagem,
            notificationId = notificationId
        )

        return Result.success()
    }
}