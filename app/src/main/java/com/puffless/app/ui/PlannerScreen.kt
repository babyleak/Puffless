package com.puffless.app.ui

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.puffless.app.viewmodel.PuffViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlannerScreen(viewModel: PuffViewModel, onBack: () -> Unit) {
    val futureStats = viewModel.recentStats.filter {
        val today = getToday()
        it.date > today
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

        Text("📅 Установить лимит на дату", style = MaterialTheme.typography.h6)

        LimitInputBlock(viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        Text("📆 Будущие лимиты:", style = MaterialTheme.typography.h6)

        FutureLimitsList(futureStats, viewModel)
    }
}

@Composable
fun LimitInputBlock(viewModel: PuffViewModel) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var selectedDate by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        )
    }

    var limitInput by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            val datePicker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)

                    selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis()

            datePicker.show()
        }) {
            Text("Выбрать дату")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Выбранная дата: $selectedDate")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = limitInput,
            onValueChange = { limitInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Лимит затяжек") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val limit = limitInput.toIntOrNull()
                if (limit != null) {
                    viewModel.setPlannedLimit(selectedDate, limit)
                    limitInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить лимит")
        }
    }
}

@Composable
fun FutureLimitsList(futureStats: List<com.puffless.app.data.DailyPuffs>, viewModel: PuffViewModel) {
    var editingDay by remember { mutableStateOf<com.puffless.app.data.DailyPuffs?>(null) }
    var newLimitInput by remember { mutableStateOf("") }
    var dayToDelete by remember { mutableStateOf<com.puffless.app.data.DailyPuffs?>(null) }

    val visibleDays = remember { mutableStateListOf<String>() }

    LaunchedEffect(futureStats) {
        visibleDays.clear()
        visibleDays.addAll(futureStats.map { it.date })
    }

    LazyColumn {
        items(futureStats, key = { it.date }) { day ->
            AnimatedVisibility(
                visible = day.date in visibleDays,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📅 ${day.date}")
                        Text("Лимит: ${day.limit}, Использовано: ${day.used}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            val today = getToday()
                            val canEdit = day.date >= today

                            Button(
                                onClick = {
                                    if (canEdit) {
                                        editingDay = day
                                        newLimitInput = day.limit.toString()
                                    }
                                },
                                enabled = canEdit
                            ) {
                                Text("✏️ Изменить")
                            }

                            Button(onClick = {
                                dayToDelete = day
                            }) {
                                Text("🗑️ Удалить")
                            }
                        }
                    }
                }
            }
        }
    }

    if (dayToDelete != null) {
        AlertDialog(
            onDismissRequest = { dayToDelete = null },
            title = { Text("Подтвердите удаление") },
            text = { Text("Вы уверены, что хотите удалить лимит на ${dayToDelete?.date}?") },
            confirmButton = {
                Button(
                    onClick = {
                        visibleDays.remove(dayToDelete!!.date)
                        viewModel.deletePlannedLimit(dayToDelete!!.date)
                        dayToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { dayToDelete = null }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    if (editingDay != null) {
        AlertDialog(
            onDismissRequest = { editingDay = null },
            title = { Text("Изменить лимит для ${editingDay?.date}") },
            text = {
                OutlinedTextField(
                    value = newLimitInput,
                    onValueChange = { newLimitInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Новый лимит") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val newLimit = newLimitInput.toIntOrNull()
                    if (newLimit != null) {
                        viewModel.setPlannedLimit(editingDay!!.date, newLimit)
                    }
                    editingDay = null
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(onClick = { editingDay = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

private fun getToday(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}