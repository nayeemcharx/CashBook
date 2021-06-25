package com.example.cashbook.fragments

import com.google.firebase.Timestamp

class History(private val amount: Double, private val check: String, private val dealer: String, private val timestamp: Timestamp, private val tranId: String) {


    private var note="-"
    fun setNote(note: String)
    {
        this.note=note
    }
    fun getAmount():String
    {
        return amount.toString()
    }
    fun getCheck():String
    {
        return check
    }
    fun getDealer():String
    {
        return dealer
    }
    fun getDate():String
    {
        val datepart=timestamp.toDate().toString().split("\\s".toRegex())
        return datepart[1]+"-"+datepart[2]+"-"+datepart[5]+" "+datepart[3]+"(GMT)"
    }
    fun getNote():String
    {
        return note
    }
    fun getTranId():String
    {
        return tranId
    }


}