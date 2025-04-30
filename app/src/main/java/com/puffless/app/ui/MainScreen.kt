package com.puffless.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.puffless.app.ui.theme.ThemeSetting
import com.puffless.app.viewmodel.PuffViewModel

@Composable
fun MainScreen(viewModel: PuffViewModel,
               onNavigateStats: () -> Unit,
               onNavigatePlanner: () -> Unit,
               onNavigateSettings: () -> Unit
              ) {
    val day = viewModel.dayData
    val context = LocalContext.current

    val progress = if (day != null && day.limit > 0) {
        ((day.limit - day.used).toFloat() / day.limit.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val targetColor = when {
        progress > 0.5f -> Color(0xFF4CAF50)
        progress > 0.2f -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 600)
    )

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

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.LightGray)
        ) {
            LinearProgressIndicator(
                progress = progress,
                color = animatedColor,
                backgroundColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

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

        Button(onClick = onNavigatePlanner) {
            Text("📆 Планировщик лимитов")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Выбери тему:", textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.saveThemeSetting(context, ThemeSetting.LIGHT) }) {
                Text("Светлая")
            }
            Button(onClick = { viewModel.saveThemeSetting(context, ThemeSetting.DARK) }) {
                Text("Темная")
            }
            Button(onClick = { viewModel.saveThemeSetting(context, ThemeSetting.SYSTEM) }) {
                Text("Системная")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onNavigateSettings() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Настройки")
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