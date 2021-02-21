package ru.mrrobot1413.lesson8homework.viewHolders

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.R

class ProgressViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    fun bind() {

    }
}