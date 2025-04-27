package com.puffless.app.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Message
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puffless.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationHelper {
    private const val CHANNEL_ID = "puffless_channel"
    private const val CHANNEL_NAME = "Puffless Notifications"
    private const val CHANNEL_DESCRIPTION = "Напоминания о лимитах затяжек"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendSimpleNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify((0..10000).random(), builder.build())
        }
    }

    fun shouldSendNotification(context: Context): Boolean {
        val prefs = context.getSharedPreferences("puffless_prefs", Context.MODE_PRIVATE)
        val lastDate = prefs.getString("last_notification_date", null)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return lastDate != today
    }

    fun markNotificationSent(context: Context) {
        val prefs = context.getSharedPreferences("puffless_prefs", Context.MODE_PRIVATE)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit().putString("last_notification_date", today).apply()
    }
}