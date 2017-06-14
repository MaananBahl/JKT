package com.example.ishan.jkt.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishan.jkt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;

public class edit_profile extends AppCompatActivity implements View.OnClickListener{

    int CAPTURE_IMAGE = 1, SELECT_IMAGE = 2;
    Button image_button, username_button, password_button;
    ImageView profile_image;
    TextView username_tv;
    FirebaseAuth fb_authorize;
    FirebaseDatabase fb_database;
    FirebaseUser fb_user;
    DatabaseReference db_ref;
    String user_id;
    SharedPreferences shared_pref;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Profile");
        ab.setDisplayHomeAsUpEnabled(true);

        username_tv = (TextView)findViewById(R.id.textView5);
        profile_image = (ImageView)findViewById(R.id.profile_image2);
        image_button = (Button)findViewById(R.id.button4);
        password_button = (Button)findViewById(R.id.button7);
        username_button = (Button)findViewById(R.id.button6);
        image_button.setOnClickListener(this);
        password_button.setOnClickListener(this);
        username_button.setOnClickListener(this);
        profile_image.setOnClickListener(this);

        //SETTING VALUES
        shared_pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        user_id = shared_pref.getString("userid",null);

        fb_database = FirebaseDatabase.getInstance();
        fb_authorize = FirebaseAuth.getInstance();
        fb_user = fb_authorize.getCurrentUser();
        db_ref = fb_database.getReference().child("users").child(user_id);
    }

    @Override
    public void onResume(){
        super.onResume();
        username_tv.setText(shared_pref.getString("name", null));
        String pic_string = shared_pref.getString("pic",null);
        if(pic_string != null) {
            byte[] imageBytes = Base64.decode(pic_string, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profile_image.setImageBitmap(decodedImage);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==image_button){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Select Mode");
            builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    i.putExtra("crop", "true");
                    i.putExtra("scale", true);
                    i.putExtra("outputX", 256);
                    i.putExtra("outputY", 256);
                    i.putExtra("aspectX", 1);
                    i.putExtra("aspectY", 1);
                    i.putExtra("return-data", true);
                    startActivityForResult(i, SELECT_IMAGE);
                }
            });
            builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, CAPTURE_IMAGE);
                }
            });
            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String imageString = getString(R.string.defalut_image);
                    db_ref.child("pic").setValue(imageString);
                    SharedPreferences.Editor editor = shared_pref.edit();
                    editor.remove("pic");
                    editor.putString("pic",imageString);
                    editor.apply();
                    onResume();
                }
            });
            builder.setTitle("Change Profile Picture");
            builder.show();
        }
        else if(v==username_button){
            Intent i = new Intent(this, edit_username.class);
            startActivity(i);
        }
        else if(v == password_button){
            Intent i = new Intent(this, edit_password.class);
            startActivity(i);
        }
        else if(v == profile_image){
            Intent i = new Intent(this, profile_image.class);
            startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;
            final Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = extras.getParcelable("data");

                //ENCODE TO BASE64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                db_ref.child("pic").setValue(imageString);
                SharedPreferences.Editor editor = shared_pref.edit();
                editor.remove("pic");
                editor.putString("pic",imageString);
                editor.apply();
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
