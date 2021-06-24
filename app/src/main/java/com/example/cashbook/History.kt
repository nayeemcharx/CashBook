package com.example.cashbook

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class History {

    // property (data member)
    private var isOn: Boolean = false

    // member function
    fun turnOn() {
        isOn = true
    }

    // member function
    fun turnOff() {
        isOn = false
    }
    fun createHistory():ArrayList<String>
    {

        val db: FirebaseFirestore
        val itemList=ArrayList<String>()
//        itemList.add("1")
        db= FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document("nayeem@gmail.com")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("test", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                itemList.add("1")
                Log.d("test", "Current data: ${snapshot.data}")
            } else {
                Log.d("test", "Current data: null")
            }
        }
        return itemList
    }
}