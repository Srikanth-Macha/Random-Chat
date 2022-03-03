package com.example.randomchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseActivity extends AppCompatActivity {
    Button startChatting, userProfile, signOut;
    String email;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    ArrayList<String> allUsers = new ArrayList<>();

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        startChatting = findViewById(R.id.chattingView);
        userProfile = findViewById(R.id.profileView);
        signOut = findViewById(R.id.signOut);

        progressBar = findViewById(R.id.progressBarInChoose);
        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(getString(R.string.database_URL));

        if (database.getReference().getKey() == null) {

            // Push the data of signed in user into firebase database
            database.getReference()
                    .child("users")
                    .child(mAuth.getUid())
                    .setValue(new User(mAuth.getUid(), email, "Sample"));
        } else {
            Toast.makeText(this, database.getReference().getKey(), Toast.LENGTH_SHORT).show();
        }

        // To find a receiver to chat with
        getReceiver();

        progressBar.setVisibility(View.GONE);

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this, UserProfile.class);
                i.putExtra("email", email);

                startActivity(i);
            }
        });

        startChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this, ChatActivity.class);
                i.putExtra("users", allUsers);

                startActivity(i);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.getReference().child("users").child(mAuth.getUid()).removeValue();

                Toast.makeText(ChooseActivity.this, mAuth.getUid(), Toast.LENGTH_SHORT).show();

                mAuth.signOut();

                startActivity(new Intent(ChooseActivity.this, SignInActivity.class));

                finish();
            }
        });

    }

    // Getting all users ids to find a user to chat with
    void getReceiver() {
        String currentUserID = mAuth.getUid();

        DatabaseReference reference = database.getReference().child("users");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot values : snapshot.getChildren()) {
                    if (!values.getKey().equals(currentUserID))
                        allUsers.add(values.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChooseActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        reference.addValueEventListener(listener);

    }
}