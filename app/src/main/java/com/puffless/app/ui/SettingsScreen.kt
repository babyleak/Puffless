package com.puffless.app.ui

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.puffless.app.ui.theme.ThemeSetting
import com.puffless.app.utils.NotificationHelper.scheduleDailyReminder
import com.puffless.app.viewmodel.PuffViewModel
import kotlin.random.Random

data class UserNotification(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean
)

@Composable
fun SettingsScreen(viewModel: PuffViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("PufflessPrefs", Context.MODE_PRIVATE)
    val gson = remember { Gson() }

    var notifications by remember {
        mutableStateOf(loadNotifications(prefs, gson))
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var tempHour by remember { mutableStateOf(9) }
    var tempMinute by remember { mutableStateOf(0) }

    val currentTheme = viewModel.themeSetting

    fun saveNotifications(list: List<UserNotification>) {
        prefs.edit().putString("notifications", gson.toJson(list)).apply()
    }

    fun addNotification(hour: Int, minute: Int) {
        val new = UserNotification(id = Random.nextInt(), hour = hour, minute = minute, enabled = true)
        val updated = notifications + new
        saveNotifications(updated)
        notifications = updated
        scheduleDailyReminder(context, hour, minute)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("👈 Назад")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("🔔 Уведомления", style = MaterialTheme.typography.h6)

        LazyColumn {
            items(notifications) { notif ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏰ %02d:%02d".format(notif.hour, notif.minute))
                    Switch(
                        checked = notif.enabled,
                        onCheckedChange = { checked ->
                            val updated = notif.copy(enabled = checked)
                            val newList = notifications.map {
                                if (it.id == notif.id) updated else it
                            }
                            saveNotifications(newList)
                            notifications = newList
                        }
                    )
                    IconButton(onClick = {
                        val newList = notifications.filterNot { it.id == notif.id }
                        saveNotifications(newList)
                        notifications = newList
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            showTimePicker = true
        }) {
            Text("➕ Добавить уведомление")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("🎨 Выбор темы", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.saveThemeSetting(context, ThemeSetting.LIGHT) },
                enabled = currentTheme != ThemeSetting.LIGHT
            ) { Text("Светлая") }

            Button(
                onClick = { viewModel.saveThemeSetting(context, ThemeSetting.DARK) },
                enabled = currentTheme != ThemeSetting.DARK
            ) { Text("Тёмная") }

            Button(
                onClick = { viewModel.saveThemeSetting(context, ThemeSetting.SYSTEM) },
                enabled = currentTheme != ThemeSetting.SYSTEM
            ) { Text("Системная") }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                addNotification(hour, minute)
                showTimePicker = false
            },
            tempHour,
            tempMinute,
            true
        ).show()
    }
}

private fun loadNotifications(prefs: SharedPreferences, gson: Gson): List<UserNotification> {
    val json = prefs.getString("notifications", null) ?: return emptyList()
    val type = object : TypeToken<List<UserNotification>>() {}.type
    return gson.fromJson(json, type)
}
