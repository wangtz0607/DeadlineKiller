package com.deadlinekiller.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deadlinekiller.R
import com.deadlinekiller.data.Deadline
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SimpleDeadlineListAdapter(private val onClickDeadlineItem: (Deadline) -> Unit) :
    ListAdapter<Deadline, SimpleDeadlineListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            onClickDeadlineItem,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_simple_deadline, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val onClickDeadlineItem: (Deadline) -> Unit, view: View
    ) : RecyclerView.ViewHolder(view) {
        private val textViewTitle = view.findViewById<TextView>(R.id.text_view_title)
        private val textViewDateTime = view.findViewById<TextView>(R.id.text_view_date_time)

        fun bind(deadline: Deadline) {
            textViewTitle.text = deadline.title
            textViewDateTime.text = deadline.dateTime.atZone(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
            )
            textViewTitle.apply {
                if (deadline.isDone) {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    setTextColor(context.getColor(R.color.gray_600))
                } else {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    setTextColor(context.getColor(R.color.black))
                }
            }
            itemView.setOnClickListener {
                onClickDeadlineItem(deadline)
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