package com.example.cashbook.fragments

import android.app.Activity
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class TransferFragment(activity: Activity) : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var transferButton:Button
    private lateinit var transferAmount:EditText
    private lateinit var receiverEmail:EditText
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

        transferButton.setOnClickListener{
            if(!transferAmount.text.isEmpty() && !receiverEmail.text.isEmpty()) {
                val amount = transferAmount.text.toString().toDouble()
                val receiver: String = receiverEmail.text.toString().trim()
                val ref: DocumentReference = db.collection("Users").document(sender)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(ref)
                    val newBalance = snapshot.getDouble("balance")!! - amount
                    if(newBalance>=0.00)
                    {
                        db.collection(receiver).get().addOnSuccessListener {
                            if (!it.isEmpty && sender != receiver) {

                                transfer(sender,receiver,amount)
                                updateHistory(sender,receiver,amount)
                                show(amount.toString()+" Tk. transfered to "+receiver)
                            }
                            else {
                                show("Please fill in a valid email address")
                            }
                        }
                    }
                    else
                    {
                        activity?.runOnUiThread{show("Out of balance")}

                    }
                    // Success
                    null
                }.addOnSuccessListener { Log.d("trans", "Transaction success!") }
                        .addOnFailureListener { e -> Log.w("trans", "Transaction failure.", e) }
            }
            else
            {
                show("please fill in the information")
            }

        }

    }

    private fun show(message:String) {
        Toast.makeText(
                activity?.baseContext, message, Toast.LENGTH_SHORT).show()

    }

    private fun updateHistory(sender: String, receiver: String, amount: Double) {
        val historyRefSender: DocumentReference = db.collection(sender).document()
        val currDate = Date()
        val senderData = hashMapOf(
                "sent_or_received" to true,
                "amount" to amount,
                "date" to currDate,
                "dealer" to receiver
        )
        historyRefSender.set(senderData).addOnSuccessListener { Log.d("test", "sender history successfully written!") }
                .addOnFailureListener { e -> Log.w("test", "Error writing document", e) }

        val historyRefReceiver: DocumentReference = db.collection(receiver).document()
        val receiverData = hashMapOf(
                "sent_or_received" to false,
                "amount" to amount,
                "date" to currDate,
                "dealer" to sender
        )
        historyRefReceiver.set(receiverData).addOnSuccessListener { Log.d("test", "receiver history successfully written!") }
                .addOnFailureListener { e -> Log.w("test", "Error writing document", e) }
    }

    private fun transfer(sender: String, receiver: String, amount: Double) {
        val transDocRef: DocumentReference = db.collection("Users").document(sender)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(transDocRef)
            val newBalance = snapshot.getDouble("balance")!! - amount
            transaction.update(transDocRef, "balance", newBalance)
            // Success
            null
        }.addOnSuccessListener { Log.d("trans", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("trans", "Transaction failure.", e) }
        val receivDocRef: DocumentReference = db.collection("Users").document(receiver)
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