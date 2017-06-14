package com.example.ishan.jkt.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.ishan.jkt.R;
import com.example.ishan.jkt.utility.functions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.StringTokenizer;

public class edit_username extends AppCompatActivity implements View.OnClickListener{

    EditText username_et;
    Button update_button;
    FirebaseDatabase fb_database;
    DatabaseReference db_ref;
    String user_id;
    SharedPreferences shared_pref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_username);
        functions.showKeyboard(this);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Username");
        ab.setDisplayHomeAsUpEnabled(true);

        username_et = (EditText)findViewById(R.id.editText8);
        update_button = (Button)findViewById(R.id.button3);
        update_button.setOnClickListener(this);

        fb_database = FirebaseDatabase.getInstance();

        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        user_id = shared_pref.getString("userid",null);
    }

    @Override
    public void onClick(View v) {
        update_button.setEnabled(false);
        String username = username_et.getText().toString();
        db_ref =  fb_database.getReference().child("users").child(user_id);
        db_ref.child("name").setValue(username);

        SharedPreferences.Editor editor = shared_pref.edit();
        editor.remove("name");
        editor.putString("name",username);
        editor.apply();
        functions.hideKeyboard(this);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                functions.hideKeyboard(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
