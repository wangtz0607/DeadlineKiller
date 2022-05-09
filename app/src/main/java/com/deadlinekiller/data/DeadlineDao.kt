package com.deadlinekiller.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeadlineDao {
    @Query("SELECT * FROM deadlines ORDER BY date_time")
    fun getAll(): Flow<List<Deadline>>

    @Query("SELECT * FROM deadlines WHERE is_done = 0 ORDER BY date_time")
    fun getUndone(): Flow<List<Deadline>>

    @Query("SELECT * FROM deadlines WHERE id = :id")
    fun getById(id: Int): Flow<Deadline>

    @Insert
    suspend fun insert(deadline: Deadline)

    @Insert
    suspend fun insertAll(deadlines: List<Deadline>)

    @Update
    suspend fun update(deadline: Deadline)

    @Query("UPDATE deadlines SET is_done = :isDone WHERE id = :id")
    suspend fun setDoneById(id: Int, isDone: Boolean)

    @Query("DELETE FROM deadlines WHERE id = :id")
    suspend fun deleteById(id: Int)
}