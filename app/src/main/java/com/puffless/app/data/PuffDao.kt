package com.puffless.app.data

import androidx.room.*

@Dao
interface PuffDao {
    @Query("SELECT * FROM daily_puffs WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyPuffs?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(day: DailyPuffs)

    @Update
    suspend fun update(day: DailyPuffs)

    @Delete
    suspend fun delete(day: DailyPuffs)

    @Query("SELECT * FROM daily_puffs ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentDays(limit: Int): List<DailyPuffs>
}