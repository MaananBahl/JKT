package com.example.ishan.jkt.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ishan.jkt.R;
import com.example.ishan.jkt.adapters.TicketAdapter;
import com.example.ishan.jkt.adapters.TicketAdapterReceiver;
import com.example.ishan.jkt.classes.Ticket;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class home_receiver extends AppCompatActivity implements View.OnClickListener{

    TextView username_tv, designation_tv;
    CircleImageView profile_image;
    RecyclerView ticket_rv;
    TicketAdapterReceiver ticket_Adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Ticket> tickets_list =new ArrayList<>();
    FirebaseDatabase fb_database;
    SharedPreferences shared_pref;
    String role;
    Boolean show_only_open = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_receiver);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Home");

        username_tv = (TextView)findViewById(R.id.textView2);
        designation_tv = (TextView) findViewById(R.id.textView4);
        profile_image = (CircleImageView)findViewById(R.id.profile_image1);
        profile_image.setOnClickListener(this);

        ticket_rv = (RecyclerView)findViewById(R.id.ticket_rv);
        ticket_Adapter = new TicketAdapterReceiver(tickets_list,this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ticket_rv.setLayoutManager(layoutManager);
        ticket_rv.setAdapter(ticket_Adapter);

        fb_database = FirebaseDatabase.getInstance();
        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        role = shared_pref.getString("role",null);
    }

    @Override
    public void onResume() {
        super.onResume();
        username_tv.setText(shared_pref.getString("name", null));
        designation_tv.setText(shared_pref.getString("designation", null));
        String pic_string = shared_pref.getString("pic", null);
        if (pic_string != null) {
            byte[] imageBytes = Base64.decode(pic_string, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profile_image.setImageBitmap(decodedImage);
        }
        update_list();
    }
    public void update_list(){

        final ProgressDialog progress_dialog = new ProgressDialog(this,R.style.MySpinnerThemeDark);
        progress_dialog.setCancelable(false);
        progress_dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress_dialog.show();

        tickets_list.clear();
        DatabaseReference ref = fb_database.getReference().child("tickets");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String Key = dataSnapshot.getKey();
                Ticket t = dataSnapshot.getValue(Ticket.class);
                if(t.getDepartment().equals(role)) {
                    t.setKey(Key);
                    if (t.getStatus().equals("Open")) {
                        long diff, diffHours = 0;

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String curr = date_format.format(c.getTime());
                        Date d1, d2;

                        try {
                            d1 = date_format.parse(curr);
                            d2 = date_format.parse(t.getCreate_date_time());
                            diff = d1.getTime() - d2.getTime();
                            diffHours = diff / (60 * 60 * 1000) % 24;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (diffHours >= 24) {
                            DatabaseReference ref = fb_database.getReference().child("tickets").child(Key);
                            ref.child("status").setValue("Close");
                            t.setStatus("Time Out");
                        }
                    }
                    if(!show_only_open){
                        tickets_list.add(t);
                    }
                    else{
                        if(!t.getStatus().equals("Close")){
                            tickets_list.add(t);
                        }
                    }
                    ticket_Adapter.notifyDataSetChanged();
                }
                progress_dialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_list();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                update_list();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                update_list();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent i1 = new Intent(this, edit_profile.class);
                startActivity(i1);
                return true;
            case R.id.sign_out:
                shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.edit();
                editor.clear();
                editor.apply();
                Intent i2 = new Intent(this, MainActivity.class);
                startActivity(i2);
                finish();
                return true;
            case R.id.show1:
                show_only_open = false;
                onResume();
                return true;
            case R.id.show2:
                show_only_open = true;
                onResume();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, profile_image.class);
        startActivity(i);
    }
}
