package com.deadlinekiller.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.data.ReminderRepository
import com.deadlinekiller.data.ReminderType
import com.deadlinekiller.services.AlarmService
import com.deadlinekiller.utils.NotificationUtils
import com.deadlinekiller.utils.VibrationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        val reminderDeadline =
            requireNotNull(intent.getParcelableExtra<FullReminder>(EXTRA_FULL_REMINDER))
        when (reminderDeadline.type) {
            ReminderType.NOTIFICATION -> {
                NotificationManagerCompat.from(context).notify(
                    reminderDeadline.id,
                    NotificationUtils.buildReminderNotification(context, reminderDeadline)
                        .setAutoCancel(true)
                        .build(),
                )
                VibrationUtils.vibrateForNotification(VibrationUtils.getVibrator(context))
                runBlocking(Dispatchers.IO) {
                    reminderRepository.setInvokedById(reminderDeadline.id, true)
                }
            }
            ReminderType.ALARM -> {
                AlarmService.start(
                    context,
                    requireNotNull(intent.getParcelableExtra(EXTRA_FULL_REMINDER)),
                )
            }
        }
    }

    companion object {
        const val ACTION = "com.deadlinekiller.REMINDER"
        const val EXTRA_FULL_REMINDER = "full_reminder"
    }
}
