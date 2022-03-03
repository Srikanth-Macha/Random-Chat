package com.example.randomchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    TextInputEditText emailView, passwordView;
    Button goButton;

    String emailText;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailView = findViewById(R.id.emailText);
        passwordView = findViewById(R.id.passwordText);
        goButton = findViewById(R.id.proceedButton);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(getString(R.string.database_URL));
        storage = FirebaseStorage.getInstance();

        goButton.setOnClickListener((view) -> {

            progressBar.setVisibility(View.VISIBLE);

            emailText = Objects.requireNonNull(emailView.getText()).toString();

            if (emailText.isEmpty() || emailText.length() <= 7 ||
                    !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(SignInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                emailView.setError("Invalid Email");

                progressBar.setVisibility(View.GONE);

            } else if (Objects.requireNonNull(passwordView.getText()).toString().length() < 8) {
                passwordView.setError("Password too short");

                Toast.makeText(SignInActivity.this, "Password too short", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);

            } else {
                mAuth.createUserWithEmailAndPassword(emailText, passwordView.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    //If user is new (never signed in before)

                                    Toast.makeText(SignInActivity.this, "Signed In", Toast.LENGTH_SHORT).show();

                                    signIn();
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                    mAuth.signInWithEmailAndPassword(emailText, passwordView.getText().toString())
                                            .addOnSuccessListener(authResult -> {
                                                //if user already exists

                                                Toast.makeText(SignInActivity.this, "Logged In", Toast.LENGTH_SHORT).show();

                                                signIn();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(SignInActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                                                passwordView.setError("Wrong password");

                                                progressBar.setVisibility(View.GONE);
                                            });
                                }
                            }
                        });
            }

        });

    }

    private void signIn() {
        Intent i = new Intent(SignInActivity.this, ChooseActivity.class);
        i.putExtra("email", emailText);

        createUser(emailText);

        progressBar.setVisibility(View.GONE);

        startActivity(i);
        finish();
    }

    private void createUser(String email) {

        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        if (uid.isEmpty())
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show();

        // Push the data of signed in user into firebase database
        database.getReference()
                .child("users")
                .child(uid)
                .setValue(new User(uid, email, "Sample"));

    }
}
