package com.myway.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myway.R;
import com.myway.models.ChatMessage;

public class ChatroomActivity extends AppCompatActivity {

    private RecyclerView messages;
    private EditText to_send;
    private ImageButton send_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        messages = findViewById(R.id.rv_chatroom_messages);
        to_send = findViewById(R.id.et_chatroom_to_send);
        send_message = findViewById(R.id.bt_chatroom_send_message);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(to_send.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()));
            }
        });

    }
}