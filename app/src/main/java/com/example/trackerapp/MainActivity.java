package com.example.trackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity  {
    Button allVehicles;
    Button addVehicle;
    Button trackAllVehicles;
    public Realm realm;
    private int networkFlag=0;
    List<String> latList = new ArrayList<String>();
    List<String> lonList = new ArrayList<String>();
    List<LatLng> latlonList = new ArrayList<LatLng>();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                networkFlag = 1;
                syncLatestData();

                System.out.println("!!!!!!!!The default network is now: " + network);
            }
            @Override
            public void onLost(Network network) {
                System.out.println("!!!!!!!!The application no longer has a default network. The last default network was " + network);
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                System.out.println("!!!!!!!!The default network changed capabilities: " + networkCapabilities);
            }

            @Override
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                System.out.println("!!!!!!!!The default network changed link properties: " + linkProperties);
            }
        });

        System.out.println("!!!!!!! NETWORK STATUS - !!!!!!!! - "+networkFlag);
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

    public void syncLatestData(){
        try {
            System.out.println("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-ykkzh/service/tracking-data-api/incoming_webhook/webhook0");
            URL url = new URL("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-ykkzh/service/tracking-data-api/incoming_webhook/webhook0");
            String readLine = null;
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("GET");
            int responseCode = conection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conection.getInputStream()));
                ArrayList<String> response = new ArrayList<>();

                while ((readLine = in.readLine()) != null) {
                    response.add(readLine);
                }
                in.close();
                // TODO: Parse the result and display in maps
                JSONArray response_json_array = new JSONArray(response.get(0));
                System.out.println("JSON String Result " + response_json_array);
            }

            System.out.println("|||||||||-LATLON TIMELINE-|||||||||");
        } catch (Exception e){
            System.out.println("EXCEPTION: "+e);
        }
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
        Intent intent = new Intent(this, ShowAllVehiclesActivity.class);
        Log.v("INFO>>","The track all vehicle activity started");
        startActivity(intent);
    }
}