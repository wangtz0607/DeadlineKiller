package com.deadlinekiller

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.data.ReminderRepository
import com.deadlinekiller.utils.AlarmManagerUtils
import com.deadlinekiller.utils.NotificationUtils
import com.deadlinekiller.utils.VibrationUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(
            this,
            NotificationChannel(
                REMINDERS_CHANNEL_ID,
                getString(R.string.reminders_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            )
        )
        MainScope().launch(Dispatchers.Default) {
            var oldList = listOf<FullReminder>()
            reminderRepository.awaitingInvocation.collect { newList ->
                oldList.forEach {
                    AlarmManagerUtils.cancelReminderById(this@MainApplication, it.id)
                }
                var hasUnsuccessfulInvocation = false
                newList.forEach {
                    if (Instant.now() > it.dateTime.plusSeconds(300)) {
                        hasUnsuccessfulInvocation = true
                    } else {
                        AlarmManagerUtils.scheduleReminder(this@MainApplication, it)
                    }
                }
                if (hasUnsuccessfulInvocation) {
                    NotificationManagerCompat.from(this@MainApplication).notify(
                        UNSUCCESSFUL_INVOCATION_NOTIFICATION_ID,
                        NotificationUtils.buildUnsuccessfulInvocationNotification(this@MainApplication)
                            .build()
                    )
                    VibrationUtils.vibrateForNotification(VibrationUtils.getVibrator(this@MainApplication))
                }
                oldList = newList
            }
        }
    }

    companion object {
        const val REMINDERS_CHANNEL_ID = "reminders"
        private const val UNSUCCESSFUL_INVOCATION_NOTIFICATION_ID = 0
    }
}
