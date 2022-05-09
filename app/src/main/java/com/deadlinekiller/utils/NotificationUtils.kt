package com.deadlinekiller.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.deadlinekiller.MainApplication
import com.deadlinekiller.R
import com.deadlinekiller.activities.MainActivity
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.fragments.ViewDeadlineFragment
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object NotificationUtils {
    private fun getNotificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(context: Context, channel: NotificationChannel) {
        getNotificationManager(context).createNotificationChannel(channel)
    }

    fun buildReminderNotification(context: Context, fullReminder: FullReminder) =
        Notification.Builder(context, MainApplication.REMINDERS_CHANNEL_ID)
            .setContentTitle(fullReminder.deadlineTitle)
            .setContentText(
                fullReminder.deadlineDateTime.atZone(ZoneId.systemDefault()).format(
                    DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
                )
            )
            .setSmallIcon(R.drawable.ic_baseline_checklist_24)
            .setContentIntent(
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.view_deadline_fragment)
                    .setArguments(
                        bundleOf(
                            ViewDeadlineFragment.ARG_DEADLINE_ID to fullReminder.deadlineId,
                            ViewDeadlineFragment.ARG_COMING_FROM_REMINDER to fullReminder.toReminder()
                        )
                    )
                    .setComponentName(MainActivity::class.java)
                    .createPendingIntent()
            )

    fun buildUnsuccessfulInvocationNotification(context: Context) =
        Notification.Builder(context, MainApplication.REMINDERS_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.unsuccessful_invocation_notification))
            .setSmallIcon(R.drawable.ic_baseline_error_24)
}
