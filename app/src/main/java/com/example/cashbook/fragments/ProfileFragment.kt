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
import java.util.*


class ProfileFragment(activity: Activity) : Fragment()
{

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var logOutButton:Button
    private lateinit var balance:TextView
    private lateinit var fullName:TextView
    private lateinit var spentMoney:TextView
    private lateinit var receivedMoney:TextView
    private lateinit var withdrawnMoney:TextView

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
        spentMoney=view.findViewById(R.id.spent_money)
        receivedMoney=view.findViewById(R.id.received_money)
        withdrawnMoney=view.findViewById(R.id.withdraw_money)


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
        updateMoneyText(1,email)
        updateMoneyText(2,email)
        updateMoneyText(3,email)
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
                    val adder="৳"+money.toString()
                    if(order==1) spentMoney.setText(adder)
                    if(order==2) receivedMoney.setText(adder)
                    if(order==3)
                    {
                        val newAdder="You have withdrawn ${adder} in this month."
                        withdrawnMoney.setText(newAdder)
                    }
                }
            }
            else
            {
                val adder="৳0"
                if(order==1) spentMoney.setText(adder)
                if(order==2) receivedMoney.setText(adder)
                if(order==3)
                {
                    val newAdder="You have withdrawn ৳0 in this month."
                    withdrawnMoney.setText(newAdder)
                }
                Log.d("tesss", "Current data: null")
            }
        }
    }

}