package com.deadlinekiller.utils

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationUtils {
    fun getVibrator(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrateForNotification(vibrator: Vibrator) {
        vibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(250, 250, 250, 250), -1),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()
        )
    }

    fun vibrateForAlarm(vibrator: Vibrator) {
        vibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(1000, 1000), 0),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
    }
}
