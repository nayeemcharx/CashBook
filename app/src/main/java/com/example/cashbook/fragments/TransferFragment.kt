package com.example.cashbook.fragments

import android.app.Activity
import android.os.Bundle
import android.os.ResultReceiver
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
import androidx.fragment.app.FragmentActivity
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import org.w3c.dom.Text
import java.util.*


class TransferFragment(activity: Activity) : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var transferButton:Button
    private lateinit var transferAmount:EditText
    private lateinit var receiverEmail:EditText
    private lateinit var balanceBefore:TextView
    private lateinit var balanceAfter:TextView
    private lateinit var transferNote:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        val sender:String = currentUser!!.email!!
        transferButton=view.findViewById(R.id.transfer_button)
        transferAmount=view.findViewById(R.id.transfer_amount)
        receiverEmail=view.findViewById(R.id.receiver_email)
        balanceBefore=view.findViewById(R.id.current_balance_transfer)
        balanceAfter=view.findViewById(R.id.balance_after_transaction)
        transferNote=view.findViewById(R.id.transfer_note)

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

        transferAmount.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(s.toString()==".")
                    transferAmount.setText("")
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

        transferButton.setOnClickListener{
            if(!transferAmount.text.isEmpty() && !receiverEmail.text.isEmpty()) {
                val note = if(transferNote.text.isEmpty()) "-" else transferNote.text.toString()
                val amount =  String.format("%.2f", transferAmount.text.toString().toDouble()).toDouble()
                val receiver: String = receiverEmail.text.toString().trim()
                val ref: DocumentReference = db.collection("Users").document(sender)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(ref)
                    val newBalance = snapshot.getDouble("balance")!! - amount
                    if(newBalance>=0.00)
                    {
                        db.collection("Users").document(receiver).get().addOnSuccessListener {
                            if (it.exists() && sender != receiver) {

                                transfer(sender,receiver,amount)
                                updateHistory(sender,receiver,amount,note)
                                show("$amount Tk. transfered to $receiver")
                            }
                            else {
                                if(it.exists()) show("You cant transfer to yourself")
                                else show("Please fill in a valid mail address")
                            }
                        }
                    }
                    else
                    {
                        activity?.runOnUiThread{show("Out of balance")}

                    }
                    // Success
                    null
                }.addOnSuccessListener { Log.d("Transfer", "Transaction success!") }
                        .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }
            }
            else
            {
                show("please fill in the information")
            }

        }

    }

    private fun initViews(sender: String) {
        receiverEmail.setText("")
        transferAmount.setText("")
        balanceAfter.setText("-")
        transferNote.setText("")
    }

    private fun show(message:String) {
        Toast.makeText(
                activity?.baseContext, message, Toast.LENGTH_SHORT).show()

    }

    private fun updateHistory(sender: String, receiver: String, amount: Double,tranNote:String) {
        val historyRefSender: DocumentReference = db.collection(sender).document()
        val currDate = Date()
        val senderData = hashMapOf(
                "sent|rec|with" to 0,
                "amount" to amount,
                "date" to currDate,
                "dealer" to receiver,
                "note" to tranNote
        )
        historyRefSender.set(senderData).addOnSuccessListener { Log.d("Transfer", "sender history successfully written!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Error writing document", e) }

        val historyRefReceiver: DocumentReference = db.collection(receiver).document()
        val receiverData = hashMapOf(
                "sent|rec|with" to 1,
                "amount" to amount,
                "date" to currDate,
                "dealer" to sender,
                "note" to tranNote
        )
        historyRefReceiver.set(receiverData).addOnSuccessListener { Log.d("Transfer", "receiver history successfully written!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Error writing document", e) }

        val month=currDate.toString().split("\\s".toRegex())[1]+currDate.toString().split("\\s".toRegex())[5]

        val thisMonthTranRef: DocumentReference = db.collection("monthlyTransfer").document(sender)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(thisMonthTranRef)
            if(snapshot.getDouble(month)==null)
            {
                val data = hashMapOf(
                        month to amount)
                transaction.set(thisMonthTranRef,data)
            }
            else
            {
                val newBalance = snapshot.getDouble(month)!! + amount
                transaction.update(thisMonthTranRef, month, newBalance)
            }
            // Success
            null
        }.addOnSuccessListener { Log.d("Transfer", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }

        val thisMonthRecRef: DocumentReference = db.collection("monthlyReceived").document(receiver)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(thisMonthRecRef)
            if(snapshot.getDouble(month)==null)
            {
                val data = hashMapOf(
                        month to amount)
                transaction.set(thisMonthRecRef,data)
            }
            else
            {
                val newBalance = snapshot.getDouble(month)!! + amount
                transaction.update(thisMonthRecRef, month, newBalance)
            }
            // Success
            null
        }.addOnSuccessListener { Log.d("Transfer", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }
    }

    private fun transfer(sender: String, receiver: String, amount: Double) {

        val transDocRef: DocumentReference = db.collection("Users").document(sender)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(transDocRef)
            val newBalance = snapshot.getDouble("balance")!! - amount
            transaction.update(transDocRef, "balance", newBalance)
            // Success
            null
        }.addOnSuccessListener {
            initViews(sender)
            Log.d("Transfer", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }


        val receivDocRef: DocumentReference = db.collection("Users").document(receiver)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(receivDocRef)
            val newBalance = snapshot.getDouble("balance")!! + amount
            transaction.update(receivDocRef, "balance", newBalance)
            // Success
            null
        }.addOnSuccessListener { Log.d("Transfer", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }

    }

}