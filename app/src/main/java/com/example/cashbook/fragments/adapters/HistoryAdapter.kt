package com.example.cashbook.fragments.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cashbook.R
import com.example.cashbook.fragments.History

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
        //val tranId=constraintLayout.getChildAt(3) as TextView
        val note=constraintLayout.getChildAt(3) as TextView
        dateTime.text=item[position].getDate()
        if(item[position].getCheck()=="1")
            amount.setTextColor(Color.GREEN)
        else if(item[position].getCheck()=="2")
            amount.setTextColor(Color.parseColor("#ff6600"))
        else
            amount.setTextColor(Color.RED)
        val amountTxt="Tk. "+item[position].getAmount()
        amount.text=amountTxt
        val beforeTxt=if(item[position].getCheck()=="0") "Transfered to " else if(item[position].getCheck()=="1") "Received from " else "Withdrawn via "
        val dealerTxt=beforeTxt+item[position].getDealer()
        dealer.text=dealerTxt
        val tranIdTxt="Tran. ID:"+item[position].getTranId()
        //tranId.text=tranIdTxt
        val noteTxt=if(item[position].getNote()=="-") "" else "Note:" +item[position].getNote()
        note.text=noteTxt


    }

    override fun getItemCount(): Int {
        return item.size
    }
}