package com.deadlinekiller.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Deadline::class,
            parentColumns = ["id"],
            childColumns = ["deadline_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deadline_id"])],
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "deadline_id") val deadlineId: Int,
    @ColumnInfo(name = "type") val type: ReminderType,
    @ColumnInfo(name = "date_time") val dateTime: Instant,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean,
    @ColumnInfo(name = "is_invoked") val isInvoked: Boolean,
) : Parcelable {
    fun toFullReminder(deadline: Deadline) = FullReminder(
        id, deadlineId, type, dateTime, isEnabled, isInvoked,
        deadline.title, deadline.dateTime, deadline.notes, deadline.isDone,
    )
}
