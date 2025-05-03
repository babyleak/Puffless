package com.puffless.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.puffless.app.viewmodel.PuffViewModel
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.graphics.toColorInt
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.textComponent

@Composable
fun StatsScreen(viewModel: PuffViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.loadRecentStats()
    }

    val chartEntries = viewModel.recentStats
        .sortedBy { it.date }
        .mapIndexed { index, day ->
            com.patrykandpatrick.vico.core.entry.FloatEntry(
                x = index.toFloat(),
                y = day.limit.toFloat()
            )
        }
    val chartEntryModelProducer = remember(chartEntries) {
        ChartEntryModelProducer(chartEntries)
    }

    val shortDates = viewModel.recentStats
        .sortedBy { it.date }
        .map { dateString ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString.date)
                outputFormat.format(date ?: "")
            } catch (e: Exception) {
                ""
            }
        }

    val labelColor = MaterialTheme.colors.onBackground

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = onBack) {
            Text("👈 Назад")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "📈 Динамика лимита затяжек",
            style = MaterialTheme.typography.h6
        )

        Spacer(Modifier.height(16.dp))

        Chart(
            chart = lineChart(
                lines = listOf(
                    LineChart.LineSpec(
                        lineColor = "#3F51B5".toColorInt(),
                        lineThicknessDp = 3f
                    )
                )
            ),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(
                label = textComponent { color = labelColor.toArgb() },
                tick = LineComponent (color = labelColor.toArgb())
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = DateAxisValueFormatter(shortDates),
                label = textComponent { color = labelColor.toArgb() },
                tick = LineComponent (color = labelColor.toArgb()),
                itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 1),
                guideline = null,
                labelRotationDegrees = 0f
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "📋 Статистика по дням",
            style = MaterialTheme.typography.h6
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(viewModel.recentStats.sortedByDescending { it.date }) { day ->
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