package com.example.cashbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity()
{
    private lateinit var email: EditText
    private lateinit var pin: EditText
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        email= findViewById(R.id.email_adress)
        pin=findViewById(R.id.pin)
        auth= FirebaseAuth.getInstance()
    }


     public fun navToSignUp(view: View)
     {
         val intent : Intent = Intent(this,SignUpActivity::class.java)
         startActivity(intent)

    }
    public fun navToHome(view: View){
        //auth.createUserWithEmailAndPassword("something@gmail.com","333333")
        val emailTxt=email.text.toString()
        val pinTxt=pin.text.toString()
        login(emailTxt,pinTxt)

    }

    private fun login(emailTxt: String, pinTxt: String)
    {

        Log.d("before", emailTxt)
        Log.d("before", pinTxt)
        auth.signInWithEmailAndPassword(emailTxt,pinTxt).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful)
            {
                Log.d("test", "signInWithEmail:success")
                val user = auth.currentUser
                val intent : Intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finish()
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



}