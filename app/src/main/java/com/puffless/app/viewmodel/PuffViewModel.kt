package com.puffless.app.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.puffless.app.data.DailyPuffs
import com.puffless.app.data.PuffDatabase
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

class PuffViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {
    private val db = PuffDatabase.getDatabase(application)
    private val dao = db.puffDao()
    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private var lastLoadedDate: String = getTodayDate()

    var dayData by mutableStateOf<DailyPuffs?>(null)
        private set

    var recentStats by mutableStateOf<List<DailyPuffs>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            loadToday()
        }
    }

    private suspend fun loadToday() {
        val data = dao.getByDate(today) ?: DailyPuffs(today, limit = 200, used = 0)
        dao.insert(data)
        dayData = data
    }

    fun usePuff() {
        viewModelScope.launch {
            dayData?.let {
                val updated = it.copy(used = it.used + 1)
                dao.update(updated)
                dayData = updated
            }
        }
    }

    fun updateLimit(newLimit: Int) {
        viewModelScope.launch {
            dayData?.let {
                val updated = it.copy(limit = newLimit)
                dao.update(updated)
                dayData = updated
            }
        }
    }

    fun loadRecentStats(limit: Int = 14) {
        viewModelScope.launch {
            recentStats = dao.getRecentDays(limit)
        }
    }

    fun setPlannedLimit(date: String, limit: Int) {
        viewModelScope.launch {
            val existing = dao.getByDate(date)
            val entry = if (existing != null) {
                existing.copy(limit = limit)
            } else {
                DailyPuffs(date = date, limit = limit, used = 0)
            }
            dao.insert(entry)

            recentStats = dao.getRecentDays(14)

            if (date == getTodayDate()) loadToday()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppResume() {
        val currentDate = getTodayDate()
        if (currentDate != lastLoadedDate) {
            viewModelScope.launch {
                val data = dao.getByDate(currentDate) ?: DailyPuffs(currentDate, limit = 200, used = 0)
                dao.insert(data)
                dayData = data
                lastLoadedDate = currentDate
            }
        }
    }

    fun getLimitChartEntries(): List<ChartEntry> {
        return recentStats.sortedBy { it.date }
            .mapIndexed { index, day ->
                FloatEntry(
                    x = index.toFloat(),
                    y = day.limit.toFloat()
                )
            }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}