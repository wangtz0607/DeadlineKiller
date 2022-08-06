package com.deadlinekiller.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.preference.PreferenceManager
import com.deadlinekiller.R
import com.deadlinekiller.activities.MainActivity
import com.deadlinekiller.data.Deadline
import com.deadlinekiller.data.DeadlineRepository
import com.deadlinekiller.fragments.ViewDeadlineFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class DeadlineWidget : AppWidgetProvider() {
    @Inject
    lateinit var deadlineRepository: DeadlineRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) = runBlocking(Dispatchers.IO) {
        for (appWidgetId in appWidgetIds) {
            PreferenceManager
                .getDefaultSharedPreferences(context)
                .getInt(KEY_DEADLINE_ID_OF_WIDGET + appWidgetId, 0)
                .let { deadlineId ->
                    deadlineRepository.getById(deadlineId).firstOrNull()?.let { deadline ->
                        updateWidget(
                            context,
                            appWidgetManager,
                            appWidgetId,
                            deadline,
                        )
                    }
                }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            for (appWidgetId in appWidgetIds) {
                remove(KEY_DEADLINE_ID_OF_WIDGET + appWidgetId)
            }
            apply()
        }
    }

    companion object {
        const val KEY_DEADLINE_ID_OF_WIDGET = "deadline_id_of_widget_"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            deadline: Deadline,
        ) {
            RemoteViews(context.packageName, R.layout.deadline_widget).apply {
                setTextViewText(R.id.text_view_title, deadline.title)
                deadline.dateTime.atZone(ZoneId.systemDefault()).let {
                    setTextViewText(
                        R.id.text_view_date_time,
                        it.format(DateTimeFormatter.ofPattern("y/MM/dd HH:mm"))
                    )
                    ChronoUnit.DAYS.between(LocalDate.now(), it.toLocalDate()).toInt().let { days ->
                        setTextViewText(R.id.text_view_days_remaining, days.toString())
                        setTextViewText(
                            R.id.text_view_days_plurals,
                            context.resources.getQuantityText(R.plurals.days, days)
                        )
                    }
                    if (Instant.now() > deadline.dateTime) {
                        setTextColor(
                            R.id.text_view_days_remaining,
                            context.getColor(R.color.red_500)
                        )
                        setTextColor(
                            R.id.text_view_days_plurals,
                            context.getColor(R.color.red_500)
                        )
                    } else {
                        setTextColor(
                            R.id.text_view_days_remaining,
                            context.getColor(R.color.blue_700)
                        )
                        setTextColor(
                            R.id.text_view_days_plurals,
                            context.getColor(R.color.blue_700)
                        )
                    }
                }
                setOnClickPendingIntent(
                    R.id.linear_layout_widget,
                    NavDeepLinkBuilder(context)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.view_deadline_fragment)
                        .setArguments(
                            bundleOf(
                                ViewDeadlineFragment.ARG_DEADLINE_ID to deadline.id,
                            )
                        )
                        .setComponentName(MainActivity::class.java)
                        .createPendingIntent()
                )
                appWidgetManager.updateAppWidget(appWidgetId, this)
            }
        }
    }
}
