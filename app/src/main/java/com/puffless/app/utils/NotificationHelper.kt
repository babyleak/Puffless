package com.puffless.app.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puffless.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.*

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

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}