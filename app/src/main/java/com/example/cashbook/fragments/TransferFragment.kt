package com.example.cashbook.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cashbook.AppConstants.AppConstants
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

import java.util.*


class TransferFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var transferButton:Button
    private lateinit var transferAmount:EditText
    private lateinit var receiverEmail:EditText
    private lateinit var balanceBefore:TextView
    private lateinit var balanceAfter:TextView
    private lateinit var transferNote:EditText


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
                    balanceBefore.text = balance.toString()
            }

        }

        initViews()

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
                            val balAfter= if(s.isNotEmpty())
                                    (if(balance - String.format("%.2f",s.toString().toDouble()).toDouble()>=0.0)
                                        String.format("%.2f",balance - String.format("%.2f",s.toString().toDouble()).toDouble()).toDouble()
                                    else "-")
                            else "-"
                            balanceBefore.text = balance.toString()
                            balanceAfter.text = balAfter.toString()
                        }
                    }
                }
            }
        })

        transferButton.setOnClickListener{
            if(transferAmount.text.isNotEmpty() && receiverEmail.text.isNotEmpty()) {
                val note = if(transferNote.text.isEmpty()) "-" else transferNote.text.toString()
                val amount =  String.format("%.2f", transferAmount.text.toString().toDouble()).toDouble()
                val receiver: String = receiverEmail.text.toString().trim()
                val ref: DocumentReference = db.collection("Users").document(sender)

                db.runTransaction{ transaction ->
                    val snapshot = transaction.get(ref)
                    val newBalance = snapshot.getDouble("balance")!! - amount
                    if(newBalance>=0.00)
                    {
                        db.collection("Users").document(receiver).get().addOnSuccessListener {
                            if (it.exists() && sender != receiver)
                            {
                                transferConfirm(sender,receiver,amount,note)
                            }
                            else
                            {
                                if(it.exists())
                                    show("You cant transfer to yourself")
                                else
                                    show("Please fill in a valid mail address")
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

    private fun transferConfirm(sender: String, receiver: String, amount: Double, note: String) {

        val dialogeView=LayoutInflater.from(activity).inflate(R.layout.confirm_pin_dialog,null)
        val myBuilder= androidx.appcompat.app.AlertDialog.Builder(requireActivity()).setView(dialogeView)
        val dialog=myBuilder.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val pinTxt:EditText? = dialog.findViewById(R.id.Confirm_pin_text)
        val confirmButton:Button? = dialog.findViewById(R.id.conirm_pin_button)

        val tranText="Transfer"
        confirmButton?.text = tranText
        confirmButton?.setOnClickListener {
            Log.d("testt", "Button detected")
            val pin: String = pinTxt?.text.toString()
            if (pin.isEmpty())
            {
                activity?.runOnUiThread{show("Enter correct pin")}
                Log.d("testt", "Confirmation empty")
            } else
            {

                auth.signInWithEmailAndPassword(sender, pin).addOnCompleteListener(requireActivity())
                { task ->
                    if (task.isSuccessful) {
                        Log.d("testt", "Confirmation successful")
                        transfer(sender,receiver,amount)
                        updateHistory(sender,receiver,amount,note)
                        activity?.runOnUiThread{show("$amount Tk. transfered to $receiver")}
                        dialog.dismiss()
                    }
                    else
                    {
                        Log.w("testt", "signInWithEmail:failure", task.exception)
                        activity?.runOnUiThread{show("Enter correct pin")}
                    }
                }
            }
        }
    }

    private fun initViews() {
        receiverEmail.setText("")
        transferAmount.setText("")
        balanceAfter.text = "-"
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
                transaction.update(thisMonthTranRef, month, String.format("%.2f",newBalance).toDouble())
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
                transaction.update(thisMonthRecRef, month, String.format("%.2f",newBalance).toDouble())
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
            transaction.update(transDocRef, "balance", String.format("%.2f",newBalance).toDouble())
            // Success
            null
        }.addOnSuccessListener {
            initViews()
            Log.d("Transfer", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }


        val receivDocRef: DocumentReference = db.collection("Users").document(receiver)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(receivDocRef)
            val newBalance = snapshot.getDouble("balance")!! + amount
            transaction.update(receivDocRef, "balance", String.format("%.2f",newBalance).toDouble())
            // Success
            null
        }.addOnSuccessListener {
            getToken("$sender sent ৳$amount to your account",receiver)
            Log.d("Transfer", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("Transfer", "Transaction failure.", e) }

    }

    private fun getToken(message: String,receiver: String)
    {


        db.collection("Tokens").document(receiver).get().addOnSuccessListener {
            if (it.exists())
            {
                val token=it["token"].toString().trim()
                val to = JSONObject()
                val data = JSONObject()
                Log.d("testt",":p")

                data.put("title", "Cashbook")
                data.put("message", message)

                to.put("to", token)
                to.put("data", data)
                sendNotification(to)
            }
            else
            {
                Log.d("Transfer","token doesnt exist")
            }
        }

    }

    private fun sendNotification(to: JSONObject)
    {

        val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                AppConstants.NOTIFICATION_URL,
                to,
                com.android.volley.Response.Listener { response: JSONObject ->

                    Log.d("testt", "onResponse: $response")
                },
                com.android.volley.Response.ErrorListener {

                    Log.d("testt", "onError: $it")
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(activity)
        request.retryPolicy = DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Log.d("testt",":p")
        requestQueue.add(request)
    }

}