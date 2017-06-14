package com.example.ishan.jkt.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.ishan.jkt.R;

public class profile_image extends AppCompatActivity {

    ImageView image;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_image);

        image = (ImageView)findViewById(R.id.imageView4);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile photo");
        ab.setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        String pic_string = pref.getString("pic",null);
        if(pic_string != null){
            byte[] imageBytes = Base64.decode(pic_string, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            image.setImageBitmap(decodedImage);
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
