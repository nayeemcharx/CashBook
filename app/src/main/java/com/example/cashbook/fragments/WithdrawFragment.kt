package com.example.cashbook.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.cashbook.HomeActivity
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class WithdrawFragment(activity: Activity) : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var withdrawButton: Button
    private lateinit var withdrawAmount: EditText
    private lateinit var agentEmail: EditText
    private lateinit var balanceBefore: TextView
    private lateinit var balanceAfter: TextView
    private lateinit var withdrawNote:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdraw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        val sender: String = currentUser!!.email!!
        withdrawButton = view.findViewById(R.id.withdraw_button)
        withdrawAmount = view.findViewById(R.id.withdraw_amount)
        agentEmail = view.findViewById(R.id.withdraw_email)
        balanceBefore = view.findViewById(R.id.current_balance_withdraw)
        balanceAfter = view.findViewById(R.id.balance_after_withdraw)
        withdrawNote=view.findViewById(R.id.withdraw_note)

        val balRef=db.collection("Users").document(sender)
        balRef.addSnapshotListener{ snap, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snap != null && snap.exists())
            {
                val balance=snap.getDouble("balance")
                if(balance!=null)
                    balanceBefore.setText(balance.toString())
            }

        }

        initViews(sender)

        withdrawAmount.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(s.toString()==".")
                    withdrawAmount.setText("")
                else
                {
                    db.collection("Users").document(sender).get().addOnSuccessListener {
                        if (it.exists()) {
                            val balance = it["balance"].toString().toDouble()
                            val balAfter= if(!s.isEmpty())
                                (if(balance - String.format("%.2f",s.toString().toDouble()).toDouble()>=0.0)
                                    balance - String.format("%.2f",s.toString().toDouble()).toDouble()
                                else "-")
                            else "-"
                            balanceBefore.setText(balance.toString())
                            balanceAfter.setText(balAfter.toString())
                        }
                    }
                }
            }
        })

        withdrawButton.setOnClickListener {
            if (!withdrawAmount.text.isEmpty() && !agentEmail.text.isEmpty()) {
                val note = if(withdrawNote.text.isEmpty()) "-" else withdrawNote.text.toString()
                val amount = String.format("%.2f", withdrawAmount.text.toString().toDouble()).toDouble()
                val receiver: String = agentEmail.text.toString().trim()
                val ref: DocumentReference = db.collection("Users").document(sender)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(ref)
                    val newBalance = snapshot.getDouble("balance")!! - amount
                    if (newBalance >= 0.00) {
                        db.collection("Agents").document(receiver).get().addOnSuccessListener {
                            if (it.exists() && sender != receiver) {

                                withdraw(sender, receiver,amount)
                                updateHistory(sender, receiver, amount,note)
                                show("$amount Tk. withdrawn via $receiver")
                            } else {
                                if (it.exists()) show("You cant transfer to yourself")
                                else show("Please fill in a valid Agent mail address")
                            }
                        }
                    } else {
                        activity?.runOnUiThread { show("Out of balance") }

                    }
                    // Success
                    null
                }.addOnSuccessListener { Log.d("trans", "Transaction success!") }
                        .addOnFailureListener { e -> Log.w("trans", "Transaction failure.", e) }
            } else {
                show("please fill in the information")
            }

        }

    }
    private fun initViews(sender: String) {
        agentEmail.setText("")
        withdrawAmount.setText("")
        balanceAfter.setText("-")
        withdrawNote.setText("")
    }

    private fun show(message: String) {
        Toast.makeText(
                activity?.baseContext, message, Toast.LENGTH_SHORT).show()

    }

    private fun updateHistory(sender: String, agent: String, amount: Double,tranNote:String) {
        val historyRefSender: DocumentReference = db.collection(sender).document()
        val currDate = Date()
        val senderData = hashMapOf(
                "sent|rec|with" to 2,
                "amount" to amount,
                "date" to currDate,
                "dealer" to agent,
                "note" to tranNote
        )
        historyRefSender.set(senderData).addOnSuccessListener { Log.d("test", "sender history successfully written!") }
                .addOnFailureListener { e -> Log.w("test", "Error writing document", e) }

        val month=currDate.toString().split("\\s".toRegex())[1]+currDate.toString().split("\\s".toRegex())[5]
        val thisMonthWithRef: DocumentReference = db.collection("monthlyWithdraw").document(sender)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(thisMonthWithRef)
            Log.d("tess","test2")
            if(snapshot.getDouble(month)==null)
            {
                val data = hashMapOf(
                        month to amount)
                transaction.set(thisMonthWithRef,data)
                Log.d("tess","yess")
            }
            else
            {
                val newBalance = snapshot.getDouble(month)!! + amount
                transaction.update(thisMonthWithRef, month, newBalance)
                Log.d("tess","tess")
            }
            Log.d("tess","last")
            // Success
            null
        }.addOnSuccessListener { Log.d("trans", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("trans", "Transaction failure.", e) }

    }

    private fun withdraw(sender: String,agent: String, amount: Double) {
        val transDocRef: DocumentReference = db.collection("Users").document(sender)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(transDocRef)
            val newBalance = snapshot.getDouble("balance")!! - amount
            transaction.update(transDocRef, "balance", newBalance)
            // Success
            null
        }.addOnSuccessListener {
            initViews(sender)
            Log.d("trans", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("trans", "Transaction failure.", e) }
        val receivDocRef: DocumentReference = db.collection("Agents").document(agent)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(receivDocRef)
            val newBalance = snapshot.getDouble("balance")!! + amount
            transaction.update(receivDocRef, "balance", newBalance)

            // Success
            null
        }.addOnSuccessListener { Log.d("trans", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("trans", "Transaction failure.", e) }

    }

}
