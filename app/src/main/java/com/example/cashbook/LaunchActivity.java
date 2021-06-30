package com.example.cashbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    ImageView image;
    TextView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(android.R.style.Theme_Black_NoTitleBar_Fullscreen,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_launch);
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image=findViewById(R.id.app_icon);
        logo=findViewById(R.id.appname);
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent=new Intent(LaunchActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }, 5000);
    }
}