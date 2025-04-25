package com.puffless.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.puffless.app.viewmodel.PuffViewModel

@Composable
fun MainScreen(viewModel: PuffViewModel, onNavigateStats: () -> Unit) {
    val day = viewModel.dayData

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text("Сегодняшний лимит: ${day?.limit ?: "-"}")
        Text("Осталось: ${(day?.limit ?: 0) - (day?.used ?: 0)}")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.usePuff() }) {
            Text("Сделал затяжку")
        }

        Spacer(modifier = Modifier.height(32.dp))

        LimitSetter(viewModel)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateStats) {
            Text("📈 Статистика")
        }
    }
}

@Composable
fun LimitSetter(viewModel: PuffViewModel) {
    var input by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Установить лимит на сегодня")

        OutlinedTextField(
            value = input,
            onValueChange = { input = it.filter { c -> c.isDigit() } },
            label = { Text("Лимит затяжек") }
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick ={
            val limit = input.toIntOrNull()
            if (limit != null) {
                viewModel.updateLimit(limit)
                input = ""
            }
        })  {
            Text("Сохранить")
        }
    }

}