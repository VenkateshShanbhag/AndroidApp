package com.example.trackerapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DialogActivity extends AppCompatActivity {

    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
            Log.v("Intent Value", value);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        TextView textView = (TextView) findViewById(R.id.display_msg);
        textView.setText(value);
    }

//    void showCustomDialog() {
//        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//
//        //then we will inflate the custom alert dialog xml that we created
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog, viewGroup, false);
//
//        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        //setting the view of the builder to our custom view that we already inflated
//        builder.setView(dialogView);
//
//        //finally creating the alert dialog and displaying it
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
}