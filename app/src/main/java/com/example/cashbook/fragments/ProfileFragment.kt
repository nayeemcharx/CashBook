package com.example.cashbook.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.cashbook.LoginActivity
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class ProfileFragment : Fragment()
{

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var logOutButton:Button
    private lateinit var balance:TextView
    private lateinit var fullName:TextView
    private lateinit var spentMoney:TextView
    private lateinit var receivedMoney:TextView
    private lateinit var withdrawnMoney:TextView

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
        spentMoney=view.findViewById(R.id.spent_money)
        receivedMoney=view.findViewById(R.id.received_money)
        withdrawnMoney=view.findViewById(R.id.withdraw_money)


        val currentUser = auth.currentUser
        val email:String = currentUser!!.email!!
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
                val bal=snapshot.getDouble("balance")

                if(fullname!=null)
                {
                    Log.d("full name",fullname)
                    fullName.text = fullname
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
                    val balanceText= "৳$bal"
                    balance.text = balanceText

                }

            }
            else
            {
                Log.d("test", "Current data: null")
            }
        }
        updateMoneyText(1,email)
        updateMoneyText(2,email)
        updateMoneyText(3,email)
        logOutButton=view.findViewById(R.id.log_out_button)
        logOutButton.setOnClickListener{
            auth.signOut()
            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


        //some code about updating profile picture

    }

    private fun updateMoneyText(order: Int,email:String)
    {
        val choose = if(order==1)"monthlyTransfer" else if(order==2) "monthlyReceived" else "monthlyWithdraw"
        val spent: DocumentReference = db.collection(choose).document(email)
        spent.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w("test", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists())
            {
                val currDate= Date()
                val month=currDate.toString().split("\\s".toRegex())[1]+currDate.toString().split("\\s".toRegex())[5]
                val money=snapshot.getDouble(month)
                if(money!=null)
                {
                    val adder= "৳$money"
                    if(order==1) spentMoney.text = adder
                    if(order==2) receivedMoney.text = adder
                    if(order==3)
                    {
                        val newAdder="You have withdrawn $adder in this month."
                        withdrawnMoney.text = newAdder
                    }
                }
            }
            else
            {
                val adder="৳0"
                if(order==1) spentMoney.text = adder
                if(order==2) receivedMoney.text = adder
                if(order==3)
                {
                    val newAdder="You have withdrawn ৳0 in this month."
                    withdrawnMoney.text = newAdder
                }
                Log.d("tesss", "Current data: null")
            }
        }
    }

}