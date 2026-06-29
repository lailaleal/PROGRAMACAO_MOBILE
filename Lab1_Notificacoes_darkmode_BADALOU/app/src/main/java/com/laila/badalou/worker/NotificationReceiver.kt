package com.laila.badalou.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra("titulo") ?: "Badalou"
        val mensagem = intent.getStringExtra("mensagem") ?: "Está na hora da sua tarefa!"
        val notificationId = intent.getIntExtra("notificationId", 0)

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showNotification(
            context = context,
            titulo = titulo,
            mensagem = mensagem,
            notificationId = notificationId
        )
    }
}