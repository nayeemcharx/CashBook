package com.example.cashbook.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cashbook.*
import com.example.cashbook.fragments.adapters.historyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.collections.ArrayList


class HistoryFragment(activity: Activity) : Fragment() {

    private lateinit var Item: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerAdapter: historyAdapter
    private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager
    private lateinit var db: FirebaseFirestore
    private lateinit var refresh: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        Item = view.findViewById(R.id.history_items)
        refresh = view.findViewById(R.id.swipTorefresh)
        refresh.isRefreshing=true
        updateItemViews()
        refresh.setOnRefreshListener {
            updateItemViews()
        }

    }

    private fun updateItemViews()
    {

        val itemList = ArrayList<History>()
        val currentUser = auth.currentUser
        val email = currentUser!!.email!!
        recyclerLayoutManager = LinearLayoutManager(activity)
        recyclerAdapter = historyAdapter(itemList)
        Item.apply {
            setHasFixedSize(true)
            layoutManager = recyclerLayoutManager
            adapter = recyclerAdapter
        }
        db.collection(email).orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val amount = document["amount"] as Double
                    val srw = document["sent|rec|with"]!!.toString()
                    val dealer = document["dealer"]!!.toString()
                    val time = document["date"] as com.google.firebase.Timestamp
                    val note = document["note"]
                    val historyItem = History(amount, srw, dealer, time, document.id.toString())
                    if (note != null)
                        historyItem.setNote(note.toString())
                    itemList.add(historyItem)
                    val datepart = time.toDate().toString().split("\\s".toRegex())
                    recyclerAdapter.notifyDataSetChanged()
                    refresh.isRefreshing = false
                }
            }
            .addOnFailureListener { exception ->
                Log.w("errors", "Error getting documents: ", exception)
            }
    }



}