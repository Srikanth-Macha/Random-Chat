package com.example.randomchat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    EditText messageBox;
    Button sendButton;

    ArrayList<String> usersList;
    ArrayList<Message> messages;
    String senderID, receiverID, senderRoom, receiverRoom;

    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            // Sets toolbar title
            getSupportActionBar().setTitle("Stranger");

            // Enables back button in the toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = FirebaseDatabase.getInstance(getString(R.string.database_URL));
        mAuth = FirebaseAuth.getInstance();

        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendButton);


        usersList = getIntent().getStringArrayListExtra("users");
        messages = new ArrayList<>();

        getRandomUser();

        Toast.makeText(this, receiverID, Toast.LENGTH_SHORT).show();

        senderID = mAuth.getUid();

        senderRoom = senderID + receiverID;
        receiverRoom = receiverID + senderID;

        // To attach adapter for recycler view
        startChat();

        // To fetch data (Messages) from database
        database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Message message = ds.getValue(Message.class);
                            messages.add(message);
                        }

                        Toast.makeText(ChatActivity.this, messages.size() + "", Toast.LENGTH_SHORT).show();

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        sendButton.setOnClickListener(view -> {
            String text = messageBox.getText().toString();

            Date date = new Date();
            Message message = new Message(text, date.getTime() + "", mAuth.getUid());

            database.getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(message)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(ChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();

                        database.getReference()
                                .child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push()
                                .setValue(message);

                        messageBox.setText("");
                    });


            closeKeyboard();
        });

    }

    private void startChat() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Functionality to end the activity when back button is pressed
        finish();

        return super.onSupportNavigateUp();
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

    void getRandomUser() {
        Random random = new Random();

        if (usersList.size() > 0) {
            int pos = random.nextInt(usersList.size());

            receiverID = usersList.get(pos);
        } else {
            Toast.makeText(this, "No user is active to chat with you", Toast.LENGTH_LONG).show();

            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        database.getReference().child("chats").child(Objects.requireNonNull(mAuth.getUid())).child("messages").removeValue();
    }

}