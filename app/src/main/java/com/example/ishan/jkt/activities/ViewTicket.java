package com.example.ishan.jkt.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ishan.jkt.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewTicket extends AppCompatActivity implements View.OnClickListener{

    TextView heading;
    TextView status;
    TextView priority;
    TextView create_date_time, update_date_time;
    TextView message;
    Button chat_button, re_button;
    String key;
    FirebaseDatabase fb_database;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ticket);

        fb_database = FirebaseDatabase.getInstance();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        heading = (TextView)findViewById(R.id.textView3);
        status = (TextView)findViewById(R.id.textView7);
        priority = (TextView)findViewById(R.id.textView9);
        create_date_time = (TextView)findViewById(R.id.textView);
        update_date_time = (TextView)findViewById(R.id.textView8);
        message = (TextView)findViewById(R.id.textView10);
        re_button = (Button)findViewById(R.id.button3);
        chat_button = (Button)findViewById(R.id.button8);
        chat_button.setOnClickListener(this);
        re_button.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        heading.setText(extras.getString("heading",null));
        status.setText(extras.getString("status",null));
        priority.setText(extras.getString("priority",null));
        message.setText(extras.getString("message",null));
        create_date_time.setText("Created : " + extras.getString("create_date_time",null));
        update_date_time.setText("Updated : " + extras.getString("update_date_time",null));
        key = extras.getString("key",null);

        if (extras.getString("status",null).equals("Close")){
            re_button.setText("Reopen");
        }
        else{
            re_button.setText("Update");
        }
    }

    @Override
    public void onClick(View v) {
        if(v == chat_button){
            Intent i = new Intent(this, ChatTicketHome.class);
            i.putExtra("key",key);
            startActivity(i);
        }
        else if(v == re_button){
            if(re_button.getText().toString().equals("Reopen")) {
                AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
                alert_dialog.setTitle("Confirmation to Re-Open Ticket");
                alert_dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        DatabaseReference ref = fb_database.getReference().child("tickets").child(key);
                        ref.child("status").setValue("Re-Open");
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String date_time = df.format(c.getTime());
                        ref.child("update_date_time").setValue(date_time);
                    }
                });
                alert_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert_dialog.show();
            }
            else if(re_button.getText().toString().equals("Update")){
                AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
                alert_dialog.setTitle("Confirmation to Update Ticket");
                alert_dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Bundle extras = getIntent().getExtras();
                        Intent i =new Intent(getApplicationContext(), UpdateTicket.class);
                        i.putExtra("message", extras.getString("message",null));
                        i.putExtra("heading",extras.getString("heading",null));
                        i.putExtra("key", extras.getString("key",null));
                        startActivity(i);
                    }
                });
                alert_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert_dialog.show();
            }
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
