package com.example.cashbook

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class historyAdapter(private val item:ArrayList<History>): RecyclerView.Adapter<historyAdapter.ViewHolder>() {
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
        val dateTime=constraintLayout.getChildAt(0) as TextView
        val amount=constraintLayout.getChildAt(1) as TextView
        val dealer=constraintLayout.getChildAt(2) as TextView
        val tranId=constraintLayout.getChildAt(3) as TextView
        val note=constraintLayout.getChildAt(4) as TextView
        dateTime.text=item[position].getDate()
        if(item[position].getCheck()=="1")
            amount.setTextColor(Color.GREEN)
        else if(item[position].getCheck()=="2")
            amount.setTextColor(Color.BLUE)
        else
            amount.setTextColor(Color.RED)
        amount.text="Tk. "+item[position].getAmount()
        val beforeTxt=if(item[position].getCheck()=="0") "Transfered to:" else if(item[position].getCheck()=="1") "Received from:" else "Withdrawn Via:"
        dealer.text=beforeTxt+item[position].getDealer()
        tranId.text="Tran. ID:"+item[position].getTranId()
        note.text="Note:"+item[position].getNote()



    }

    override fun getItemCount(): Int {
        return item.size
    }
}