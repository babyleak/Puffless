package com.puffless.app.ui

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.ChartValues

class DateAxisValueFormatter(
    private val dates: List<String>
) : AxisValueFormatter<AxisPosition.Horizontal.Bottom> {

    override fun formatValue(
        value: Float,
        chartValues: ChartValues
    ): CharSequence {
        val index = value.toInt()
        return dates.getOrNull(index) ?: ""
    }
}