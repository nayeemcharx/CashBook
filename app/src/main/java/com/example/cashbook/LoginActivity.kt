package com.example.cashbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity()
{
    private lateinit var email: EditText
    private lateinit var pin: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var signuptxt:TextView
    private lateinit var loginButton:Button
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // User is signed in
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            // User is signed out
            Log.d("test", "onAuthStateChanged:signed_out")
        }
        setContentView(R.layout.activity_login)
        email= findViewById(R.id.email_adress)
        pin=findViewById(R.id.pin)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        loginButton=findViewById(R.id.log_in_button)
        loginButton.setOnClickListener{

            val emailTxt=email.text.toString()
            val pinTxt=pin.text.toString()
            if(emailTxt.isEmpty() || pinTxt.isEmpty())
            {
                Toast.makeText(baseContext, "Please fill in the information",
                        Toast.LENGTH_SHORT).show()
            }
            else
            {
                login(emailTxt, pinTxt)
            }

        }

        signuptxt=findViewById(R.id.textSignUP)
        signuptxt.setOnClickListener{

            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun login(emailTxt: String, pinTxt: String)
    {


        auth.signInWithEmailAndPassword(emailTxt,pinTxt).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful)
            {
                Log.d("test", "signInWithEmail:success")
                val intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finish()
                updateToken()

            }
            else
            {
                // If sign in fails, display a message to the user.
                Log.w("test", "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("testtt", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }


            val token = task.result
            val currEmail:String = FirebaseAuth.getInstance().currentUser!!.email!!
            val documentReference: DocumentReference = db.collection("Tokens").document(currEmail)
            val data = hashMapOf(
                    "token" to token.toString().trim()
            )
            documentReference.set(data).addOnSuccessListener{ Log.d("test", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("test", "Error writing document", e) }

        })
    }

}