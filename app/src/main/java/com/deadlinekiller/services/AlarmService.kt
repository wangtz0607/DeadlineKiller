package com.deadlinekiller.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.data.ReminderRepository
import com.deadlinekiller.fragments.MainPreferenceFragment
import com.deadlinekiller.utils.NotificationUtils
import com.deadlinekiller.utils.VibrationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {
    @Inject
    lateinit var reminderRepository: ReminderRepository
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderDeadline =
            requireNotNull(intent?.getParcelableExtra<FullReminder>(EXTRA_FULL_REMINDER))
        startForeground(
            reminderDeadline.id,
            NotificationUtils.buildReminderNotification(this, reminderDeadline)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                    }
                }
                .build()
        )
        mediaPlayer =
            MediaPlayer().apply {
                setDataSource(
                    this@AlarmService,
                    MainPreferenceFragment.getRingtone(this@AlarmService)
                )
                setAudioAttributes(
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                )
                isLooping = true
                prepare()
                start()
            }
        if (MainPreferenceFragment.isVibrationEnabled(this)) {
            vibrator = VibrationUtils.getVibrator(this)
            VibrationUtils.vibrateForAlarm(checkNotNull(vibrator))
        }
        runBlocking(Dispatchers.IO) {
            reminderRepository.setInvokedById(reminderDeadline.id, true)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        vibrator?.cancel()
    }

    companion object {
        private const val EXTRA_FULL_REMINDER = "full_reminder"

        fun start(context: Context, fullReminder: FullReminder) {
            context.startForegroundService(
                Intent(context, AlarmService::class.java).apply {
                    putExtra(EXTRA_FULL_REMINDER, fullReminder)
                }
            )
        }
    }
}