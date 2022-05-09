package com.deadlinekiller.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.receivers.ReminderReceiver
import java.time.Instant

object AlarmManagerUtils {
    private fun getAlarmManager(context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(context: Context, fullReminder: FullReminder) {
        getAlarmManager(context).setAlarmClock(
            AlarmManager.AlarmClockInfo(
                fullReminder.dateTime
                    .let { dateTime ->
                        if (Instant.now() < dateTime) {
                            dateTime
                        } else {
                            Instant.now().plusMillis(500)
                        }
                    }
                    .toEpochMilli(),
                null
            ),
            PendingIntent.getBroadcast(
                context,
                fullReminder.id,
                Intent().apply {
                    action = ReminderReceiver.ACTION
                    setPackage(context.packageName)
                    putExtra(ReminderReceiver.EXTRA_FULL_REMINDER, fullReminder)
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        )
    }

    fun cancelReminderById(context: Context, id: Int) {
        getAlarmManager(context).cancel(
            PendingIntent.getBroadcast(
                context,
                id,
                Intent().apply {
                    action = ReminderReceiver.ACTION
                    setPackage(context.packageName)
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        )
    }
}
