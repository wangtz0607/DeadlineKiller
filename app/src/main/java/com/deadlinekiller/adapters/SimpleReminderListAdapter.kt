package com.deadlinekiller.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deadlinekiller.R
import com.deadlinekiller.data.Reminder
import com.deadlinekiller.data.ReminderType
import com.google.android.material.switchmaterial.SwitchMaterial
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SimpleReminderListAdapter(
    private val menuInflater: MenuInflater,
    private val isReminderHighlighted: (Reminder) -> Boolean,
    private val onClickReminderItem: (Reminder) -> Unit,
    private val onReminderEnabledChange: (Reminder, Boolean) -> Unit,
    private val onEditReminder: (Reminder) -> Unit,
    private val onDeleteReminder: (Reminder) -> Unit,
) :
    ListAdapter<Reminder, SimpleReminderListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            menuInflater,
            isReminderHighlighted,
            onClickReminderItem,
            onReminderEnabledChange,
            onEditReminder,
            onDeleteReminder,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_simple_reminder, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val menuInflater: MenuInflater,
        private val isReminderHighlighted: (Reminder) -> Boolean,
        private val onClickDeadlineItem: (Reminder) -> Unit,
        private val onReminderEnabledChange: (Reminder, Boolean) -> Unit,
        private val onEditReminder: (Reminder) -> Unit,
        private val onDeleteReminder: (Reminder) -> Unit,
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        private val imageViewReminderTypeIcon =
            view.findViewById<ImageView>(R.id.image_view_reminder_type_icon)
        private val textViewDateTime =
            view.findViewById<TextView>(R.id.text_view_deadline_date_time)
        private val imageViewErrorIcon = view.findViewById<ImageView>(R.id.image_view_error_icon)
        private val switchMaterial = view.findViewById<SwitchMaterial>(R.id.switch_material)

        fun bind(reminder: Reminder) {
            imageViewReminderTypeIcon.setImageResource(
                when (reminder.type) {
                    ReminderType.NOTIFICATION -> R.drawable.ic_baseline_chat_bubble_outline_24
                    ReminderType.ALARM -> R.drawable.ic_baseline_alarm_24
                }
            )
            textViewDateTime.text = reminder.dateTime.atZone(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
            )
            imageViewErrorIcon.visibility =
                if (
                    reminder.isEnabled
                    && !reminder.isInvoked
                    && Instant.now() > reminder.dateTime
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            switchMaterial.isChecked = reminder.isEnabled
            if (reminder.isInvoked) {
                textViewDateTime.apply {
                    setTextColor(context.getColor(R.color.gray_600))
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                switchMaterial.visibility = View.INVISIBLE
            } else {
                textViewDateTime.apply {
                    setTextColor(context.getColor(R.color.black))
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                switchMaterial.visibility = View.VISIBLE
            }
            itemView.apply {
                if (isReminderHighlighted(reminder)) {
                    setBackgroundColor(context.getColor(R.color.light_blue_50))
                } else {
                    setBackgroundColor(context.getColor(R.color.white))
                }
            }
            switchMaterial.setOnCheckedChangeListener { _, isChecked ->
                onReminderEnabledChange(reminder, isChecked)
            }
            itemView.setOnClickListener {
                onClickDeadlineItem(reminder)
            }
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menuInflater.inflate(R.menu.menu_reminder_options, menu)
                menu.findItem(R.id.item_edit).setOnMenuItemClickListener {
                    onEditReminder(reminder)
                    true
                }
                menu.findItem(R.id.item_delete).setOnMenuItemClickListener {
                    onDeleteReminder(reminder)
                    true
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Reminder>() {
            override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) =
                oldItem == newItem
        }
    }
}