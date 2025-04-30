package com.puffless.app.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val safeContext = context ?: return

            val prefs = safeContext.getSharedPreferences("PufflessPrefs", Context.MODE_PRIVATE)
            val hour = prefs.getInt("hour", 9)
            val minute = prefs.getInt("minute", 0)
            NotificationHelper.scheduleDailyReminder(context, hour, minute)
        }
    }
}