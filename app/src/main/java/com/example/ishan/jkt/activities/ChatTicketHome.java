package com.example.ishan.jkt.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ishan.jkt.R;
import com.example.ishan.jkt.adapters.ChatTicketAdapter;
import com.example.ishan.jkt.classes.Comment;
import com.example.ishan.jkt.utility.functions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatTicketHome extends AppCompatActivity implements View.OnClickListener{

    RecyclerView chat_rv;
    ArrayList<Comment> chatArrayList = new ArrayList<>();
    ChatTicketAdapter chatTicketAdapter;
    RecyclerView.LayoutManager layoutManager;
    EditText messsage_et;
    Button send_button;
    FirebaseDatabase fb_database;
    DatabaseReference db_ref;
    String user_id, key;
    SharedPreferences shared_pref;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ticket_home);
        chat_rv = (RecyclerView)findViewById(R.id.chat_rv);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Chats");
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        key = extras.getString("key",null);

        chatTicketAdapter = new ChatTicketAdapter(chatArrayList);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        chat_rv.setLayoutManager(layoutManager);
        chat_rv.setAdapter(chatTicketAdapter);
        layoutManager.scrollToPosition(chatArrayList.size()-1);

        messsage_et = (EditText)findViewById(R.id.editText6);
        send_button = (Button)findViewById(R.id.button9);
        send_button.setOnClickListener(this);
        fb_database = FirebaseDatabase.getInstance();

        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        user_id = shared_pref.getString("userid",null);
        db_ref = fb_database.getReference().child("tickets").child(key).child("chat");
        db_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment chat = dataSnapshot.getValue(Comment.class);
                if (chat.senderuid.equals(user_id)) {
                    chat.sender = "Me";
                }
                chatArrayList.add(chat);
                chatTicketAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {         }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {         }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {         }

            @Override
            public void onCancelled(DatabaseError databaseError) {        }

        });
    }

    @Override
    public void onClick(View v) {
        String message = messsage_et.getText().toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String date_time = df.format(c.getTime());
        Comment chat = new Comment(shared_pref.getString("name", null), user_id, message, date_time);
        if (key != null) {
            db_ref = fb_database.getReference().child("tickets").child(key).child("chat");
            db_ref.push().setValue(chat);
            functions.hideKeyboard(this);
            messsage_et.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
