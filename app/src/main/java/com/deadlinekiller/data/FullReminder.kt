package com.deadlinekiller.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class FullReminder(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "deadline_id") val deadlineId: Int,
    @ColumnInfo(name = "type") val type: ReminderType,
    @ColumnInfo(name = "date_time") val dateTime: Instant,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean,
    @ColumnInfo(name = "is_invoked") val isInvoked: Boolean,
    @ColumnInfo(name = "deadline_title") val deadlineTitle: String,
    @ColumnInfo(name = "deadline_date_time") val deadlineDateTime: Instant,
    @ColumnInfo(name = "deadline_notes") val deadlineNotes: String,
    @ColumnInfo(name = "deadline_is_done") val deadlineIsDone: Boolean,
) : Parcelable {
    fun toReminder() = Reminder(id, deadlineId, type, dateTime, isEnabled, isInvoked)
}
