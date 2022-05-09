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
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.data.ReminderType
import com.google.android.material.switchmaterial.SwitchMaterial
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReminderListAdapter(
    private val menuInflater: MenuInflater,
    private val onClickReminderItem: (FullReminder) -> Unit,
    private val onReminderEnabledChange: (FullReminder, Boolean) -> Unit,
    private val onEditReminder: (FullReminder) -> Unit,
    private val onDeleteReminder: (FullReminder) -> Unit
) : ListAdapter<FullReminder, ReminderListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            menuInflater,
            onClickReminderItem,
            onReminderEnabledChange,
            onEditReminder,
            onDeleteReminder,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reminder, parent, false),
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val menuInflater: MenuInflater,
        private val onClickReminderItem: (FullReminder) -> Unit,
        private val onReminderEnabledChange: (FullReminder, Boolean) -> Unit,
        private val onEditReminder: (FullReminder) -> Unit,
        private val onDeleteReminder: (FullReminder) -> Unit,
        view: View
    ) :
        RecyclerView.ViewHolder(view) {
        private val imageViewReminderTypeIcon =
            view.findViewById<ImageView>(R.id.image_view_reminder_type_icon)
        private val textViewDateTime =
            view.findViewById<TextView>(R.id.text_view_deadline_date_time)
        private val imageViewErrorIcon = view.findViewById<ImageView>(R.id.image_view_error_icon)
        private val textViewDeadlineTitle =
            view.findViewById<TextView>(R.id.text_view_deadline_title)
        private val switchMaterial = view.findViewById<SwitchMaterial>(R.id.switch_material)

        fun bind(fullReminder: FullReminder) {
            imageViewReminderTypeIcon.setImageResource(
                when (fullReminder.type) {
                    ReminderType.NOTIFICATION -> R.drawable.ic_baseline_chat_bubble_outline_24
                    ReminderType.ALARM -> R.drawable.ic_baseline_alarm_24
                }
            )
            textViewDateTime.text = fullReminder.dateTime.atZone(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
            )
            imageViewErrorIcon.visibility =
                if (
                    fullReminder.isEnabled
                    && !fullReminder.isInvoked
                    && Instant.now() > fullReminder.dateTime
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            textViewDeadlineTitle.text = fullReminder.deadlineTitle
            switchMaterial.isChecked = fullReminder.isEnabled
            if (fullReminder.isInvoked) {
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
            textViewDeadlineTitle.apply {
                paintFlags = if (fullReminder.deadlineIsDone) {
                    paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
            switchMaterial.setOnCheckedChangeListener { _, isChecked ->
                onReminderEnabledChange(fullReminder, isChecked)
            }
            itemView.setOnClickListener {
                onClickReminderItem(fullReminder)
            }
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menuInflater.inflate(R.menu.menu_reminder_options, menu)
                menu.findItem(R.id.item_edit).setOnMenuItemClickListener {
                    onEditReminder(fullReminder)
                    true
                }
                menu.findItem(R.id.item_delete).setOnMenuItemClickListener {
                    onDeleteReminder(fullReminder)
                    true
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FullReminder>() {
            override fun areItemsTheSame(oldItem: FullReminder, newItem: FullReminder) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FullReminder, newItem: FullReminder) =
                oldItem == newItem
        }
    }
}
