package com.example.cashbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class historyAdapter(private val item:ArrayList<String>): RecyclerView.Adapter<historyAdapter.ViewHolder>() {
    class ViewHolder(val constraintLayout: ConstraintLayout) :
        RecyclerView.ViewHolder(constraintLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val constraintLayout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_item_list_layout, parent, false) as ConstraintLayout

        return ViewHolder(constraintLayout)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val constraintLayout=holder.constraintLayout
        val nameTextView=constraintLayout.getChildAt(0) as TextView
        val nameIsUrgenTextView=constraintLayout.getChildAt(1) as TextView
        val dateOfItem=constraintLayout.getChildAt(2) as TextView

    }

    override fun getItemCount(): Int {
        return item.size
    }
}