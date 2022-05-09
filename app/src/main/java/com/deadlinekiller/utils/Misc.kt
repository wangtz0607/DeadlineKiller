package com.deadlinekiller.utils

import android.content.Context
import android.widget.EditText
import com.deadlinekiller.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun getDurationDescription(context: Context, duration: Duration): String {
    if (duration.isNegative) {
        return "-(${getDurationDescription(context, duration.negated())})"
    }
    val daysPart = duration.toDays().toInt()
    val hoursPart = duration.toHours().toInt() % 24
    val minutesPart = duration.toMinutes().toInt() % 60
    return if (daysPart > 0) {
        context.getString(
            R.string.duration_format_days_hours_minutes,
            daysPart,
            context.resources.getQuantityString(R.plurals.days, daysPart),
            hoursPart,
            context.resources.getQuantityString(R.plurals.hours, hoursPart),
            minutesPart,
            context.resources.getQuantityString(R.plurals.minutes, minutesPart)
        )
    } else if (hoursPart > 0) {
        context.getString(
            R.string.duration_format_hours_minutes,
            hoursPart,
            context.resources.getQuantityString(R.plurals.hours, hoursPart),
            minutesPart,
            context.resources.getQuantityString(R.plurals.minutes, minutesPart)
        )
    } else {
        context.getString(
            R.string.duration_format_minutes,
            minutesPart,
            context.resources.getQuantityString(R.plurals.minutes, minutesPart)
        )
    }
}

fun buildMaterialDatePicker(editText: EditText) =
    MaterialDatePicker.Builder.datePicker()
        .apply {
            try {
                setSelection(
                    LocalDate.parse(editText.text, DateTimeFormatter.ofPattern("y/MM/dd"))
                        .atStartOfDay()
                        .atZone(ZoneId.of("UTC"))
                        .toInstant()
                        .toEpochMilli()
                )
            } catch (e: DateTimeParseException) {
            }
        }
        .build()
        .apply {
            addOnPositiveButtonClickListener {
                editText.setText(
                    Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).format(
                        DateTimeFormatter.ofPattern("y/MM/dd")
                    )
                )
            }
        }

fun buildMaterialTimePicker(editText: EditText) =
    MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .apply {
            try {
                LocalTime.parse(
                    editText.text,
                    DateTimeFormatter.ofPattern("HH:mm")
                ).let {
                    setHour(it.hour)
                    setMinute(it.minute)
                }
            } catch (e: DateTimeParseException) {
            }
        }
        .build()
        .apply {
            addOnPositiveButtonClickListener {
                editText.setText(getString(R.string.time_format, hour, minute))
            }
        }
