package com.deadlinekiller.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeadlineRepository @Inject constructor(private val deadlineDao: DeadlineDao) {
    val all: Flow<List<Deadline>> = deadlineDao.getAll()
    val undone: Flow<List<Deadline>> = deadlineDao.getUndone()

    fun getById(id: Int): Flow<Deadline> = deadlineDao.getById(id)

    suspend fun setDoneById(id: Int, isDone: Boolean) {
        deadlineDao.setDoneById(id, isDone)
    }

    suspend fun insert(deadline: Deadline) {
        deadlineDao.insert(deadline)
    }

    suspend fun update(deadline: Deadline) {
        deadlineDao.update(deadline)
    }

    suspend fun deleteById(id: Int) {
        deadlineDao.deleteById(id)
    }
}
