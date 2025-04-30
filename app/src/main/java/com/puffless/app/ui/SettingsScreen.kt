package com.puffless.app.ui

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.puffless.app.utils.NotificationHelper.scheduleDailyReminder
import java.util.*

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("PufflessPrefs", Context.MODE_PRIVATE)

    var selectedHour by remember { mutableStateOf(prefs.getInt("hour", 9)) }
    var selectedMinute by remember { mutableStateOf(prefs.getInt("minute", 0)) }

    val timeFormatted = "%02d:%02d".format(selectedHour, selectedMinute)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Настройки", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Время уведомления: $timeFormatted")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val picker = TimePickerDialog(
                context,
                { _: TimePicker, hour: Int, minute: Int ->
                    selectedHour = hour
                    selectedMinute = minute
                    saveTimeToPrefs(prefs, hour, minute)
                    scheduleDailyReminder(context, hour, minute)
                },
                selectedHour,
                selectedMinute,
                true
            )
            picker.show()
        }) {
            Text("Выбрать время")
        }
    }
}

private fun saveTimeToPrefs(prefs: SharedPreferences, hour: Int, minute: Int) {
    prefs.edit().putInt("hour", hour).putInt("minute", minute).apply()
}