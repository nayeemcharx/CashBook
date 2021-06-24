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
import com.example.cashbook.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.auth.User


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
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        val itemList=ArrayList<String>()
        val currentUser=auth.currentUser
        val email=currentUser!!.email!!
        Item = view.findViewById(R.id.history_items)
        recyclerLayoutManager = LinearLayoutManager(activity)
        recyclerAdapter = historyAdapter(itemList)
        Item.apply {
            setHasFixedSize(true)
            layoutManager = recyclerLayoutManager
            adapter = recyclerAdapter
        }
        db.collection(email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        itemList.add("1")
                        Log.d("test", "${document.id} => ${document.data}")
                        recyclerAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("test", "Error getting documents: ", exception)
                }


    }

}