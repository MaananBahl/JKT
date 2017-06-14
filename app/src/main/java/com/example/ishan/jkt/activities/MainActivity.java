package com.example.ishan.jkt.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.ishan.jkt.R;
import com.example.ishan.jkt.classes.user;
import com.example.ishan.jkt.utility.functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailid_et, password_et;
    Button signin_button;
    FirebaseAuth fb_authorize;
    FirebaseDatabase fb_database;
    SharedPreferences shared_pref;
    String email, password, user_id, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fb_authorize = FirebaseAuth.getInstance();
        fb_database = FirebaseDatabase.getInstance();

        //CHECKING SHARED PREFERENCES
        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        email = shared_pref.getString("email_sp", null);
        password = shared_pref.getString("password_sp", null);
        role = shared_pref.getString("role",null);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (email != null && password != null) {
            setContentView(R.layout.splash_screen);
            final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
            alert_dialog.setTitle("Network Unavailable");
            alert_dialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    onResume();
                }
            });
            alert_dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            if(!isNetworkConnectionAvailable()){
                alert_dialog.show();
            }
            fb_authorize.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(role.equals("employee")) {
                            Intent i = new Intent(getApplicationContext(), home.class);
                            startActivity(i);
                        }
                        else{
                            Intent i = new Intent(getApplicationContext(), home_receiver.class);
                            startActivity(i);
                        }
                        finish();
                    }
                }
            });
        }
        else {
            setContentView(R.layout.activity_main);
            emailid_et = (EditText) findViewById(R.id.editText);
            password_et = (EditText) findViewById(R.id.editText2);
            signin_button = (Button) findViewById(R.id.button);
            signin_button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==signin_button){
            functions.hideKeyboard(this);

            //CREATE DIALOG BOX FOR ERRORS
            final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
            alert_dialog.setTitle("Unable to SignIn");
            alert_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            email = emailid_et.getText().toString();
            password = password_et.getText().toString();

            if(!isNetworkConnectionAvailable()){
                alert_dialog.setMessage("Please check Your Internet Connection");
                alert_dialog.show();
            }

            else if(!email.contains("@jktech.com")){
                alert_dialog.setMessage("Invalid Email ID, Please enter a valid JKT Email ID");
                alert_dialog.show();
            }
            else if(password.isEmpty()){
                alert_dialog.setMessage("Please Enter the Password");
                alert_dialog.show();
            }
            else {

                //CREATE A PROGRESS DIALOG
                final ProgressDialog progress_dialog = new ProgressDialog(this,R.style.MySpinnerThemeLight);
                progress_dialog.setCancelable(false);
                progress_dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                progress_dialog.show();

                //SIGN IN
                fb_authorize.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           signIn();
                        }
                        else {
                            alert_dialog.setMessage("Invalid UserID / Password");
                            alert_dialog.show();
                            progress_dialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    public void signIn(){
        final SharedPreferences.Editor editor = shared_pref.edit();
        editor.putString("email_sp", email);
        editor.putString("password_sp", password);
        StringTokenizer st = new StringTokenizer(email,"@");
        user_id = st.nextToken();

        DatabaseReference db_ref =  fb_database.getReference().child("users");
        db_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                user u = dataSnapshot.getValue(user.class);
                if(u.email.equals(email)) {
                    editor.putString("name", u.name);
                    editor.putString("designation",u.designation);
                    editor.putString("pic",u.pic);
                    editor.putString("role",u.role);
                    editor.putString("userid",user_id);
                    editor.apply();
                    if(u.role.equals("employee")) {
                        Intent i = new Intent(getApplicationContext(), home.class);
                        startActivity(i);
                    }
                    else if(u.role.equals("Software Department")){
                        Intent i = new Intent(getApplicationContext(), home_receiver.class);
                        startActivity(i);
                        FirebaseMessaging.getInstance().subscribeToTopic("ticketssoftware");
                    }
                    else if(u.role.equals("Hardware Department")){
                        Intent i = new Intent(getApplicationContext(), home_receiver.class);
                        startActivity(i);
                        FirebaseMessaging.getInstance().subscribeToTopic("ticketshardware");
                    }
                    else {
                        // Do nothing
                    }

                    finish();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}
