package com.example.ishan.jkt.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ishan.jkt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class edit_password extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseUser fb_user;
    Button change_button;
    EditText prev_et, new_et, re_et;
    SharedPreferences shared_pref;
    ImageView tick_iv1, tick_iv2, tick_iv3;
    int flag1=0, flag2=0, flag3=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Password");
        ab.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        prev_et = (EditText)findViewById(R.id.editText1);
        new_et = (EditText)findViewById(R.id.editText2);
        re_et = (EditText)findViewById(R.id.editText3);
        tick_iv1 = (ImageView)findViewById(R.id.imageView);
        tick_iv2 = (ImageView)findViewById(R.id.imageView2);
        tick_iv3 = (ImageView)findViewById(R.id.imageView3);
        change_button = (Button)findViewById(R.id.button);
        change_button.setOnClickListener(this);

        new_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len  = s.length();
                if(len>=6){
                    tick_iv2.setVisibility(View.VISIBLE);
                    flag2=1;
                }
                else{
                    tick_iv2.setVisibility(View.INVISIBLE);
                    flag2=0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        prev_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String original_password = shared_pref.getString("password_sp", null);
                String entered_password = s.toString();
                if(original_password.equals(entered_password)){
                    tick_iv1.setVisibility(View.VISIBLE);
                    flag1=1;
                }
                else{
                    tick_iv1.setVisibility(View.INVISIBLE);
                    flag1=0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        re_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass1 = new_et.getText().toString();
                String pass2 = s.toString();
                if(pass1.equals(pass2)){
                    tick_iv3.setVisibility(View.VISIBLE);
                    flag3=1;
                }
                else{
                    tick_iv3.setVisibility(View.INVISIBLE);
                    flag3=0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setTitle("Unable to SignIn");
        alert_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if(flag1==0){
            alert_dialog.setMessage("Please Enter Your Correct Previous Password");
            alert_dialog.show();
        }
        else if(flag2==0){
            alert_dialog.setMessage("New Password Length must be greater than 6");
            alert_dialog.show();
        }
        else if(flag3==0){
            alert_dialog.setMessage("New Password doesn't match");
            alert_dialog.show();
        }
        else{
            fb_user = auth.getCurrentUser();
            fb_user.updatePassword(re_et.getText().toString());
            SharedPreferences.Editor editor = shared_pref.edit();
            shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
            editor.clear();
            editor.apply();
            Intent i2 = new Intent(this, MainActivity.class);
            startActivity(i2);
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
}
