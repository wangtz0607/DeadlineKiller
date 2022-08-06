package com.deadlinekiller.widgets

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlinekiller.adapters.SimpleDeadlineListAdapter
import com.deadlinekiller.databinding.DeadlineWidgetConfigureBinding
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeadlineWidgetConfigureActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        val viewModel by viewModels<MainViewModel>()
        val binding = DeadlineWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            val adapter = SimpleDeadlineListAdapter(onClickDeadlineItem = { deadline ->
                PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                    putInt(DeadlineWidget.KEY_DEADLINE_ID_OF_WIDGET + appWidgetId, deadline.id)
                    apply()
                }
                DeadlineWidget.updateWidget(
                    this@DeadlineWidgetConfigureActivity,
                    AppWidgetManager.getInstance(this@DeadlineWidgetConfigureActivity),
                    appWidgetId,
                    deadline,
                )
                setResult(RESULT_OK, Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                })
                finish()
            })
            binding.recyclerViewSimpleDeadlineList.adapter = adapter
            binding.recyclerViewSimpleDeadlineList.layoutManager = LinearLayoutManager(this)
            viewModel.allDeadlines.observe(this) {
                adapter.submitList(it)
            }
        }
    }
}
