package com.deadlinekiller.modules

import android.content.Context
import com.deadlinekiller.data.AppDatabase
import com.deadlinekiller.data.DeadlineDao
import com.deadlinekiller.data.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        AppDatabase.getInstance(applicationContext)

    @Provides
    fun provideDeadlineDao(appDatabase: AppDatabase): DeadlineDao = appDatabase.deadlineDao()

    @Provides
    fun provideReminderDao(appDatabase: AppDatabase): ReminderDao = appDatabase.reminderDao()
}
