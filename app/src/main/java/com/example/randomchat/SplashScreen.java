package com.example.randomchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(getString(R.string.database_URL));

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        // First create a Splash Screen
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    Toast.makeText(SplashScreen.this, "Hello " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(SplashScreen.this, ChooseActivity.class);
                    i.putExtra("email", user.getEmail());

                    startActivity(i);
                } else {
                    startActivity(new Intent(SplashScreen.this, SignInActivity.class));
                }

                finish();
            }
        }, 1000); //Show splash screen for 1 second

        //After 1 second move to signIn screen

    }
}