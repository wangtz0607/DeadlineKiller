package com.deadlinekiller

import com.deadlinekiller.data.Deadline
import com.deadlinekiller.data.Reminder
import com.deadlinekiller.data.ReminderType
import java.time.Instant
import kotlin.random.Random

object TestUtils {
    fun createDeadlines(count: Int) = List(count) { i ->
        Deadline(
            id = i + 1,
            title = "Deadline ${i + 1}",
            dateTime = Instant.ofEpochSecond(
                Random.nextLong(Instant.MIN.epochSecond, Instant.MAX.epochSecond)
            ),
            notes = "Notes ${i + 1}",
            isDone = Random.nextBoolean(),
        )
    }

    fun createReminders(count: Int, deadlines: List<Deadline>) = List(count) { i ->
        Reminder(
            id = i + 1,
            deadlineId = deadlines.random().id,
            type = ReminderType.values()[Random.nextInt(ReminderType.values().size)],
            dateTime = Instant.ofEpochSecond(
                Random.nextLong(Instant.MIN.epochSecond, Instant.MAX.epochSecond)
            ),
            isEnabled = Random.nextBoolean(),
            isInvoked = Random.nextBoolean(),
        )
    }
}
