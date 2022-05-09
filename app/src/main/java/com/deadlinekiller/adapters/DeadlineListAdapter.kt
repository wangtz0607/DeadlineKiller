package com.deadlinekiller.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deadlinekiller.R
import com.deadlinekiller.data.Deadline
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DeadlineListAdapter(
    private val menuInflater: MenuInflater,
    private val onClickDeadlineItem: (Deadline) -> Unit,
    private val onEditDeadline: (Deadline) -> Unit,
    private val onDeleteDeadline: (Deadline) -> Unit,
    private val onDeadlineDoneChange: (Deadline, Boolean) -> Unit,
) :
    ListAdapter<Deadline, DeadlineListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            menuInflater,
            onClickDeadlineItem,
            onEditDeadline,
            onDeleteDeadline,
            onDeadlineDoneChange,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_deadline, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val menuInflater: MenuInflater,
        private val onClickDeadlineItem: (Deadline) -> Unit,
        private val onEditDeadline: (Deadline) -> Unit,
        private val onDeleteDeadline: (Deadline) -> Unit,
        private val onDeadlineDoneChange: (Deadline, Boolean) -> Unit,
        view: View,
    ) :
        RecyclerView.ViewHolder(view) {
        private val textViewTitle = view.findViewById<TextView>(R.id.text_view_title)
        private val textViewDateTime = view.findViewById<TextView>(R.id.text_view_date_time)
        private val linearLayoutDaysRemaining =
            view.findViewById<LinearLayout>(R.id.linear_layout_days_remaining)
        private val textViewDaysRemaining =
            view.findViewById<TextView>(R.id.text_view_days_remaining)
        private val textViewDaysPlurals =
            view.findViewById<TextView>(R.id.text_view_days_plurals)

        fun bind(deadline: Deadline) {
            textViewTitle.text = deadline.title
            deadline.dateTime.atZone(ZoneId.systemDefault()).let {
                textViewDateTime.text = it.format(DateTimeFormatter.ofPattern("y/MM/dd HH:mm"))
                ChronoUnit.DAYS.between(LocalDate.now(), it.toLocalDate()).toInt().let { days ->
                    textViewDaysRemaining.text = days.toString()
                    textViewDaysPlurals.text =
                        itemView.context.resources.getQuantityString(R.plurals.days, days)
                }
            }
            if (Instant.now() > deadline.dateTime) {
                itemView.context.getColor(R.color.red_500).let {
                    textViewDaysRemaining.setTextColor(it)
                    textViewDaysPlurals.setTextColor(it)
                }
            } else {
                itemView.context.getColor(R.color.blue_700).let {
                    textViewDaysRemaining.setTextColor(it)
                    textViewDaysPlurals.setTextColor(it)
                }
            }
            if (deadline.isDone) {
                textViewTitle.apply {
                    setTextColor(context.getColor(R.color.gray_600))
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                linearLayoutDaysRemaining.visibility = View.INVISIBLE
            } else {
                textViewTitle.apply {
                    setTextColor(context.getColor(R.color.black))
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                linearLayoutDaysRemaining.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                onClickDeadlineItem(deadline)
            }
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menuInflater.inflate(R.menu.menu_deadline_options, menu)
                menu.findItem(R.id.item_mark_as_done_or_undone).apply {
                    if (deadline.isDone) {
                        setTitle(R.string.item_mark_as_undone)
                        setOnMenuItemClickListener {
                            onDeadlineDoneChange(deadline, false)
                            true
                        }
                    } else {
                        setTitle(R.string.item_mark_as_done)
                        setOnMenuItemClickListener {
                            onDeadlineDoneChange(deadline, true)
                            true
                        }
                    }
                }
                menu.findItem(R.id.item_edit).setOnMenuItemClickListener {
                    onEditDeadline(deadline)
                    true
                }
                menu.findItem(R.id.item_delete).setOnMenuItemClickListener {
                    onDeleteDeadline(deadline)
                    true
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Deadline>() {
            override fun areItemsTheSame(oldItem: Deadline, newItem: Deadline) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Deadline, newItem: Deadline) =
                oldItem == newItem
        }
    }
}