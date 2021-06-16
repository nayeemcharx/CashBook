package com.example.cashbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity()
{
    private lateinit var firstName:EditText
    private lateinit var lastName:EditText
    private lateinit var email:EditText
    private lateinit var pin:EditText
    private lateinit var confirmPin:EditText
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        email=findViewById(R.id.emailSignUp)
        pin=findViewById(R.id.pinSignUp)
        confirmPin=findViewById(R.id.ConfirmPin)
        auth= FirebaseAuth.getInstance()
    }
    public fun signUpCLick(view: View) {

        val emailTxt: String = email.text.toString()
        val pinTxt: String = pin.text.toString()
        val confirmPinTxt: String = confirmPin.text.toString()
        if (pinTxt ==confirmPinTxt)
        {
            auth.createUserWithEmailAndPassword(emailTxt, pinTxt)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful)
                    {
                        Log.d("tag", "createUserWithEmail:success")
                        val user = auth.currentUser
                        val intent: Intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.w("tag", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
        else
        {
            Toast.makeText(
                baseContext, "Password Doesn't match.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    public fun navToLogIn(view: View)
    {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
