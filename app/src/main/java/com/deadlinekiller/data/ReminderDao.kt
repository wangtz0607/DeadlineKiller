package com.deadlinekiller.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query(
        "SELECT "
                + "reminders.*, "
                + "deadlines.title AS deadline_title, "
                + "deadlines.date_time AS deadline_date_time, "
                + "deadlines.notes AS deadline_notes, "
                + "deadlines.is_done AS deadline_is_done "
                + "FROM reminders "
                + "JOIN deadlines ON reminders.deadline_id = deadlines.id "
                + "ORDER BY reminders.date_time"
    )
    fun getAll(): Flow<List<FullReminder>>

    @Query(
        "SELECT "
                + "reminders.*, "
                + "deadlines.title AS deadline_title, "
                + "deadlines.date_time AS deadline_date_time, "
                + "deadlines.notes AS deadline_notes, "
                + "deadlines.is_done AS deadline_is_done "
                + "FROM reminders "
                + "JOIN deadlines ON reminders.deadline_id = deadlines.id "
                + "WHERE reminders.date_time > STRFTIME('%s', 'now') "
                + "ORDER BY reminders.date_time"
    )
    fun getUpcoming(): Flow<List<FullReminder>>

    @Query(
        "SELECT "
                + "reminders.*, "
                + "deadlines.title AS deadline_title, "
                + "deadlines.date_time AS deadline_date_time, "
                + "deadlines.notes AS deadline_notes, "
                + "deadlines.is_done AS deadline_is_done "
                + "FROM reminders "
                + "JOIN deadlines ON reminders.deadline_id = deadlines.id "
                + "WHERE reminders.is_enabled = 1 "
                + "AND reminders.is_invoked = 0 "
                + "ORDER BY reminders.date_time"
    )
    fun getAwaitingInvocation(): Flow<List<FullReminder>>

    @Query(
        "SELECT "
                + "reminders.*, "
                + "deadlines.title AS deadline_title, "
                + "deadlines.date_time AS deadline_date_time, "
                + "deadlines.notes AS deadline_notes, "
                + "deadlines.is_done AS deadline_is_done "
                + "FROM reminders "
                + "JOIN deadlines ON reminders.deadline_id = deadlines.id "
                + "WHERE reminders.id = :id"
    )
    fun getById(id: Int): Flow<FullReminder>

    @Query("SELECT * FROM reminders WHERE deadline_id = :deadlineId ORDER BY date_time")
    fun getByDeadlineId(deadlineId: Int): Flow<List<Reminder>>

    @Insert
    suspend fun insert(reminder: Reminder)

    @Insert
    suspend fun insertAll(reminders: List<Reminder>)

    @Update
    suspend fun update(reminder: Reminder)

    @Query("UPDATE reminders SET is_enabled = :isEnabled WHERE id = :id")
    suspend fun setEnabledById(id: Int, isEnabled: Boolean)

    @Query("UPDATE reminders SET is_enabled = :isEnabled WHERE deadline_id = :deadlineId")
    suspend fun setEnabledByDeadlineId(deadlineId: Int, isEnabled: Boolean)

    @Query("UPDATE reminders SET is_invoked = :isInvoked WHERE id = :id")
    suspend fun setInvokedById(id: Int, isInvoked: Boolean)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int)
}
