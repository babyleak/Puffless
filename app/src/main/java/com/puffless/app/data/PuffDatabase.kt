package com.puffless.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailyPuffs::class], version = 1)
abstract class PuffDatabase : RoomDatabase() {
    abstract fun puffDao(): PuffDao

    companion object {
        @Volatile
        private var INSTANCE: PuffDatabase? = null

        fun getDatabase(context: Context): PuffDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PuffDatabase::class.java,
                    "puff_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}