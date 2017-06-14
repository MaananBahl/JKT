package com.example.ishan.jkt.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ishan.jkt.R;
import com.example.ishan.jkt.classes.Ticket;
import com.example.ishan.jkt.classes.user;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NewTicket extends AppCompatActivity implements View.OnClickListener{

    EditText subject_et, message_et;
    Spinner priority_sp,department_sp;
    Button create_button;
    FirebaseDatabase fb_database;
    SharedPreferences shared_pref;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_ticket);

        fb_database = FirebaseDatabase.getInstance();

        subject_et = (EditText)findViewById(R.id.editText3);
        message_et = (EditText)findViewById(R.id.editText4);
        department_sp = (Spinner)findViewById(R.id.spinner);
        priority_sp = (Spinner)findViewById(R.id.spinner2);
        create_button = (Button)findViewById(R.id.button5);
        create_button.setOnClickListener(this);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Create New Ticket");
        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        String subject, department, priority, message, date_time, status = "Open", username, userid;
        subject = subject_et.getText().toString();
        message = message_et.getText().toString();
        department = department_sp.getSelectedItem().toString();
        priority = priority_sp.getSelectedItem().toString();
        username = shared_pref.getString("name",null);
        userid = shared_pref.getString("userid",null);

        final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setTitle("Unable to Create Ticket");
        alert_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if(subject.isEmpty()){
            alert_dialog.setMessage("Please Enter Subject!");
            alert_dialog.show();
        }
        else if(department.equals("Select Department")){
            alert_dialog.setMessage("Please select a Department!");
            alert_dialog.show();
        }
        else if(priority.equals("Select Priority")){
            alert_dialog.setMessage("Please select a Priority!");
            alert_dialog.show();
        }
        else if(message.isEmpty()){
            alert_dialog.setMessage("Please Enter Message!");
            alert_dialog.show();
        }
        else {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            date_time = df.format(c.getTime());

            Ticket t = new Ticket(subject, priority, status, date_time, message, department, username, userid, date_time);
            DatabaseReference ref = fb_database.getReference().child("tickets");
            ref.push().setValue(t);

            sendNotification(department);
            finish();
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


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification(final String department) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body","Ticket is Generated");
                    dataJson.put("title","JKT");
                    json.put("notification",dataJson);
                    if(department.equals("Software Department")){
                        json.put("to","/topics/ticketssoftware");
                    }
                    else if(department.equals("Hardware Department")){
                        json.put("to","/topics/ticketshardware");
                    }
                    else{
                        // Do nothing
                    }
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+"AIzaSyBQaYl-n8nnsHSVdvXcKpzxRaIt631ccqU ")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();
    }
}
