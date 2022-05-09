package com.deadlinekiller.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
@Entity(tableName = "deadlines")
data class Deadline(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date_time") val dateTime: Instant,
    @ColumnInfo(name = "notes") val notes: String,
    @ColumnInfo(name = "is_done") val isDone: Boolean,
) : Parcelable
