package com.puffless.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.puffless.app.viewmodel.PuffViewModel

@Composable
fun StatsScreen(viewModel: PuffViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = onBack) {
            Text("👈 Назад")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.recentStats) { day ->
                val remaining = day.limit - day.used
                val status = when {
                    remaining > 0 -> "✅ Осталось $remaining"
                    remaining == 0 -> "🔥 Лимит исчерпан"
                    else -> "⚠️ Превышен на ${-remaining}"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📅 ${day.date}")
                        Text("Лимит: ${day.limit}, Использовано: ${day.used}")
                        Text(status)
                    }
                }
            }
        }
    }
}