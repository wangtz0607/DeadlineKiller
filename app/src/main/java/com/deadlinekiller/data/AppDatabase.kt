package com.deadlinekiller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Deadline::class, Reminder::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deadlineDao(): DeadlineDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        private const val DATABASE_NAME = "deadline-killer-db"
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(applicationContext: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                applicationContext, AppDatabase::class.java, DATABASE_NAME
            ).build().also { instance = it }
        }
    }
}