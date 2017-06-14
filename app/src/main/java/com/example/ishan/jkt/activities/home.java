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
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import com.example.ishan.jkt.R;
import com.example.ishan.jkt.adapters.TicketAdapter;
import com.example.ishan.jkt.classes.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.StringTokenizer;
import de.hdodenhof.circleimageview.CircleImageView;

public class home extends AppCompatActivity implements View.OnClickListener{

    TextView username_tv, designation_tv;
    CircleImageView profile_image;
    RecyclerView ticket_rv;
    TicketAdapter ticket_Adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Ticket> tickets_list =new ArrayList<>();
    FloatingActionButton new_fab;
    FirebaseDatabase fb_database;
    String user_id;
    SharedPreferences shared_pref;
    Boolean show_only_open = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Home");


        username_tv = (TextView)findViewById(R.id.textView2);
        designation_tv = (TextView) findViewById(R.id.textView4);
        profile_image = (CircleImageView)findViewById(R.id.profile_image1);
        profile_image.setOnClickListener(this);

        new_fab = (FloatingActionButton)findViewById(R.id.fab_newticket);
        new_fab.setOnClickListener(this);

        ticket_rv = (RecyclerView)findViewById(R.id.ticket_rv);
        ticket_Adapter = new TicketAdapter(tickets_list,this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ticket_rv.setLayoutManager(layoutManager);
        ticket_rv.setAdapter(ticket_Adapter);

        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        user_id = shared_pref.getString("userid", null);

        fb_database = FirebaseDatabase.getInstance();
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
        tickets_list.clear();
        update_list();
    }
    public void update_list(){

        final ProgressDialog progress_dialog = new ProgressDialog(this, R.style.MySpinnerThemeDark);
        progress_dialog.setCancelable(false);
        progress_dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress_dialog.show();
        DatabaseReference ref = fb_database.getReference().child("tickets");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Ticket myTicket = dataSnapshot.getValue(Ticket.class);
                if(user_id.equals(myTicket.getUserid())) {
                    String Key = dataSnapshot.getKey();
                    myTicket.setKey(Key);
                    if (myTicket.getStatus().equals("Open")) {
                        long diff, diffHours = 0;

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String curr = date_format.format(c.getTime());
                        Date d1, d2;

                        try {
                            d1 = date_format.parse(curr);
                            d2 = date_format.parse(myTicket.getCreate_date_time());
                            diff = d1.getTime() - d2.getTime();
                            diffHours = diff / (60 * 60 * 1000) % 24;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (diffHours >= 24) {
                            DatabaseReference ref = fb_database.getReference().child("tickets").child(Key);
                            ref.child("status").setValue("Close");
                            myTicket.setStatus("Time Out");
                        }
                    }
                    if(!show_only_open){
                        tickets_list.add(myTicket);
                    }
                    else{
                        if(!myTicket.getStatus().equals("Close")){
                            tickets_list.add(myTicket);
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

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
        if(v == profile_image){
            Intent i = new Intent(this, profile_image.class);
            startActivity(i);
        }
        else {
            Intent intent = new Intent(this, NewTicket.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        finish();
    }
}
