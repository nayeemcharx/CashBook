package com.example.cashbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


     public fun navToSignUp(view: View){
        val intent : Intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)

    }
    public fun navToHome(view: View){
        val intent : Intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)

    }


}