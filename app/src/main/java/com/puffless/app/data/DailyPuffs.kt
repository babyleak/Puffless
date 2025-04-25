package com.puffless.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_puffs")
data class DailyPuffs (
    @PrimaryKey val date: String,
    val limit: Int,
    val used: Int
)