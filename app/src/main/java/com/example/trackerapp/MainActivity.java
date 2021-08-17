package com.example.trackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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
    Button trackAllVehicles;
    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*add new vehicle */
        addVehicle = findViewById(R.id.add_vehicle);
        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddVehiclePage();
            }
        });

        /*show all vehicles */
        allVehicles = findViewById(R.id.vehicle_details);
        allVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVehicleDetailsPage();
            }
        });

        /* Track all vehicles on map */
        trackAllVehicles = findViewById(R.id.track_vehicles);
        trackAllVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTrackVehicles();
            }
        });
    }

    private void openVehicleDetailsPage() {
        Intent intent = new Intent(this, AllVehicleDetails.class);
        Log.v("INFO","The open vehicle details page activity started");
        startActivity(intent);
    }

    private void openAddVehiclePage() {
        Intent intent = new Intent(this, AddVehicle.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }

    private void openTrackVehicles() {
        Intent intent = new Intent(this, MapsActivity2.class);
        Log.v("INFO>>","The track all vehicle activity started");
        startActivity(intent);
    }
}