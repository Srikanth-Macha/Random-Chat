package com.example.randomchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity {
    TextView nameSetUp, emailText;
    EditText nameText;
    String email;

    Button submit;

    FirebaseDatabase database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameText = findViewById(R.id.nameTextInProfile);
        emailText = findViewById(R.id.emailTextInProfile);
        nameSetUp = findViewById(R.id.nameSetUp);

        submit = findViewById(R.id.submitButton);

        database = FirebaseDatabase.getInstance(getString(R.string.database_URL));
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        emailText.setText(email);
        emailText.setClickable(false);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();

                closeKeyboard();

                if (name.isEmpty()) {
                    nameText.setError("Please enter a name");
                } else {
                    //To change name in the database
                    String uid = mAuth.getCurrentUser().getUid();

                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(new User(uid, email, name));

                    Toast.makeText(UserProfile.this, "Hi✌ " + name, Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
    }

    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}