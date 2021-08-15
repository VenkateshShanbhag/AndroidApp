package com.example.trackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.example.trackerapp.DBops.SyncRealmDB;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity  {
//    Realm realm;
//    EditText name;
//    EditText reg_num;
//    Button btnSave;
    Button allVehicles;
    Button addVehicle;
    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*add new vehicle */
        addVehicle = findViewById(R.id.add_vehicle);

        /*show all vehicles */
        allVehicles = findViewById(R.id.show_vehicles);


        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddVehiclePage();
            }
        });

        allVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVehicleDetailsPage();
            }
        });

    }

    private void openVehicleDetailsPage() {
        Intent intent = new Intent(this, AllVehicleDetails.class);
        System.out.println("This activity has been started");
        startActivity(intent);
    }

    private void openAddVehiclePage() {

        Intent intent = new Intent(this, AddVehicle.class);
        System.out.println("This activity has been started");
        startActivity(intent);

    }
}