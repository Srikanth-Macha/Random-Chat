package com.example.randomchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVED = 2;
    private final Context context;
    ArrayList<Message> messages;
    String senderRoom, receiverRoom;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://random-chat-591d8-default-rtdb.asia-southeast1.firebasedatabase.app/");

    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_message, parent, false);

            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.received_message, parent, false);

            return new ReceivedViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.senderID.equals(mAuth.getUid())) {
            // If message is sent by the current user
            return ITEM_SENT;
        } else {
            // If not sent by the current user
            return ITEM_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            viewHolder.textView.setText(message.messageText);
        } else {
            ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;

            viewHolder.textView.setText(message.messageText);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.sentMessage);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.receivedMessage);
        }
    }

}
