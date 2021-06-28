package com.example.cashbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class SignUpActivity : AppCompatActivity()
{
    private lateinit var fullName:EditText
    private lateinit var email:EditText
    private lateinit var pin:EditText
    private lateinit var confirmPin:EditText
    private lateinit var auth:FirebaseAuth
    private lateinit var cancel:TextView
    private lateinit var signUpButton:Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        fullName=findViewById(R.id.fullname)
        email=findViewById(R.id.emailSignUp)
        pin=findViewById(R.id.pinSignUp)
        confirmPin=findViewById(R.id.ConfirmPin)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        signUpButton=findViewById(R.id.Sign_Up)
        signUpButton.setOnClickListener{
            val fullnameTxt=fullName.text.toString()
            val emailTxt: String = email.text.toString()
            val pinTxt: String = pin.text.toString()
            val confirmPinTxt: String = confirmPin.text.toString()
            if(emailTxt.isEmpty() || pinTxt.isEmpty() || confirmPinTxt.isEmpty() || fullnameTxt.isEmpty())
            {
                Toast.makeText(
                        baseContext, "Please fill in the information", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if (pinTxt == confirmPinTxt)
                    signUp(emailTxt,pinTxt,fullnameTxt)
                else {
                    Toast.makeText(baseContext, "Password Doesn't match.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancel=findViewById(R.id.textLogin)
        cancel.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun signUp(emailTxt: String, pinTxt: String,fullnameTxt: String)
    {
        Log.d("test","signing Up")
        auth.createUserWithEmailAndPassword(emailTxt, pinTxt).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful)
            {
                Log.d("tag", "createUserWithEmail:success")
                val currentUser = auth.currentUser
                val email:String = currentUser!!.email!!
                val documentReference: DocumentReference = db.collection("Users").document(email)
                val currDate= Date()
                val user = hashMapOf(
                        "full_name" to fullnameTxt,
                        "creation_date" to currDate,
                        "balance" to 100
                )
                documentReference.set(user).addOnSuccessListener{ Log.d("test", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("test", "Error writing document", e) }
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
            else
            {
                Log.w("tag", "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
