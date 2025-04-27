package com.puffless.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            Text("← Назад")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("📅 Установить лимит на дату", style = MaterialTheme.typography.h6)

        LimitInputBlock(viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        Text("📆 Будущие лимиты:", style = MaterialTheme.typography.h6)

        FutureLimitsList(futureStats)
    }
}

@Composable
fun LimitInputBlock(viewModel: PuffViewModel) {
    var dateInput by remember { mutableStateOf("") }
    var limitInput by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = dateInput,
            onValueChange = { dateInput = it },
            label = { Text("Дата (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

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
                if (limit != null && dateInput.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
                    viewModel.setPlannedLimit(dateInput, limit)
                    dateInput = ""
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
fun FutureLimitsList(futureStats: List<com.puffless.app.data.DailyPuffs>) {
    LazyColumn {
        items(futureStats) { day ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📅 ${day.date}")
                    Text("Лимит: ${day.limit}, Использовано: ${day.used}")
                }
            }
        }
    }
}

private fun getToday(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}