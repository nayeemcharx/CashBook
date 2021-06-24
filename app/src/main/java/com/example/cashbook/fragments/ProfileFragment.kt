package com.example.cashbook.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.cashbook.MainActivity
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.armcha.elasticview.ElasticView
import org.w3c.dom.Text


class ProfileFragment(activity: Activity) : Fragment()
{

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var logOutButton:Button
    private lateinit var balance:TextView
    private lateinit var fullName:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
        

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        balance=view.findViewById(R.id.balance)
        fullName=view.findViewById(R.id.user)
        val currentUser = auth.currentUser
        val userID:String=currentUser!!.uid
        val email:String = currentUser.email!!
        val documentReference: DocumentReference = db.collection("Users").document(email)
        documentReference.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w("test", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists())
            {
                val fullname=snapshot.getString("full_name")
                val date=snapshot.getDate("creation_date")
                var bal=snapshot.getDouble("balance")

                if(fullname!=null)
                {
                    Log.d("full name",fullname)
                    fullName.setText(fullname)
                }
                if(date!=null)
                {
                    Log.d("full name",date.toString())
                    //textview.setText(date.toString)
                    // or modifying how date looks like instead of like "June 20, 2021 at 4:05:24 PM UTC+6"
                }
                if(bal!=null)
                {
                    Log.d("balance", bal.toString())
                    balance.setText(bal.toString())

                }

            }
            else
            {
                Log.d("test", "Current data: null")
            }
        }

        logOutButton=view.findViewById(R.id.log_out_button)
        logOutButton.setOnClickListener{
            auth.signOut()
            requireActivity().run{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


        //some code about updating profile picture

    }

}