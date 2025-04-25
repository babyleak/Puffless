package com.puffless.app.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.puffless.app.data.DailyPuffs
import com.puffless.app.data.PuffDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

class PuffViewModel(application: Application) : AndroidViewModel(application) {
    private val db = PuffDatabase.getDatabase(application)
    private val dao = db.puffDao()
    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

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
}