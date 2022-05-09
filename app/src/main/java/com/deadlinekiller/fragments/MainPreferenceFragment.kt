package com.deadlinekiller.fragments

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.deadlinekiller.R

class MainPreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var ringtonePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ringtonePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    ?.let {
                        setRingtone(requireContext(), it)
                        findPreference<Preference>(KEY_RINGTONE)?.summary =
                            RingtoneManager.getRingtone(context, it).getTitle(context)
                    }
            }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>(KEY_RINGTONE)?.summary =
            RingtoneManager.getRingtone(context, getRingtone(requireContext())).getTitle(context)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == KEY_RINGTONE) {
            ringtonePickerLauncher.launch(
                Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtras(
                        bundleOf(
                            RingtoneManager.EXTRA_RINGTONE_TYPE to RingtoneManager.TYPE_ALARM,
                            RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT to true,
                            RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT to true,
                            RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI to Settings.System.DEFAULT_ALARM_ALERT_URI,
                            RingtoneManager.EXTRA_RINGTONE_EXISTING_URI to getRingtone(
                                requireContext()
                            ),
                        )
                    )
                }
            )
            return true
        } else {
            return super.onPreferenceTreeClick(preference)
        }
    }

    companion object {
        private const val KEY_RINGTONE = "ringtone"
        private const val KEY_VIBRATE = "vibrate"

        private fun setRingtone(context: Context, ringtone: Uri) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putString(KEY_RINGTONE, ringtone.toString())
                apply()
            }
        }

        fun getRingtone(context: Context): Uri =
            PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(KEY_RINGTONE, null)?.let { Uri.parse(it) }
                ?: Settings.System.DEFAULT_ALARM_ALERT_URI

        fun isVibrationEnabled(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_VIBRATE, true)
    }
}
