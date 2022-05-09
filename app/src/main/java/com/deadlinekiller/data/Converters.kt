package com.deadlinekiller.data

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.epochSecond

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochSecond(it) }

    @TypeConverter
    fun fromReminderType(value: ReminderType?): Int? = value?.ordinal

    @TypeConverter
    fun toReminderType(value: Int?): ReminderType? = value?.let { ReminderType.values()[value] }
}