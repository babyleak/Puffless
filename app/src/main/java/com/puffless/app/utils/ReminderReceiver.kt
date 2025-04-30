package com.puffless.app.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val safeContext = context ?: return

        NotificationHelper.createNotificationChannel(safeContext)
        NotificationHelper.sendSimpleNotification(
            safeContext,
            "Не забудь",
            "Сколько затяжек осталось сегодня?"
        )
    }
}
