package com.deadlinekiller.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val reminderDao: ReminderDao) {
    val all: Flow<List<FullReminder>> = reminderDao.getAll()
    val upcoming: Flow<List<FullReminder>> = reminderDao.getUpcoming()
    val awaitingInvocation: Flow<List<FullReminder>> = reminderDao.getAwaitingInvocation()

    fun getById(id: Int): Flow<FullReminder> =
        reminderDao.getById(id)

    fun getByDeadlineId(deadlineId: Int): Flow<List<Reminder>> =
        reminderDao.getByDeadlineId(deadlineId)

    suspend fun setEnabledById(id: Int, isEnabled: Boolean) {
        reminderDao.setEnabledById(id, isEnabled)
    }

    suspend fun setEnabledByDeadlineId(deadlineId: Int, isEnabled: Boolean) {
        reminderDao.setEnabledByDeadlineId(deadlineId, isEnabled)
    }

    suspend fun setInvokedById(id: Int, hasFired: Boolean) {
        reminderDao.setInvokedById(id, hasFired)
    }

    suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    suspend fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    suspend fun deleteById(id: Int) {
        reminderDao.deleteById(id)
    }
}
