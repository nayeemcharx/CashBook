package com.example.cashbook.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashbook.MainActivity
import com.example.cashbook.R
import com.example.cashbook.historyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HistoryFragment(activity:Activity) : Fragment() {

    private lateinit var Item: RecyclerView
    private lateinit var auth:FirebaseAuth
    private lateinit var recyclerAdapter: historyAdapter
    private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager
    private lateinit var db:FirebaseFirestore
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
        var item = ArrayList<String>()
        auth= FirebaseAuth.getInstance()
        val currentUser=auth.currentUser
        val email=currentUser!!.email!!
        db= FirebaseFirestore.getInstance()
        item.add("1")
        item.add("1")
        db.collection(email).get().addOnSuccessListener {

            item.add("1")
            }
            .addOnFailureListener { exception ->
                Log.d("testing data", "Error getting documents: ", exception)
            }
        Item=view.findViewById(R.id.history_items)
        recyclerLayoutManager= LinearLayoutManager(activity)
        recyclerAdapter= historyAdapter(item)

        Item.apply {
            setHasFixedSize(true)
            layoutManager=recyclerLayoutManager
            adapter=recyclerAdapter
        }


    }

}