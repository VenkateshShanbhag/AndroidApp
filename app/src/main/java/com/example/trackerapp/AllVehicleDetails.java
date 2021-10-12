package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.trackerapp.Model.TrackingGeoSpatial;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.mongodb.App;


public class AllVehicleDetails extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String Appid;
    private App app;
    public Realm realm;
    Button home;
    Realm backgroundThreadRealm;
    private RealmResults<TrackingGeoSpatial> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication dbConfigs = new MyApplication();
        Appid = dbConfigs.getAppid();
        backgroundThreadRealm = dbConfigs.getAppConfigs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        try{
            getDataFromSync();
        } catch (Exception e)
        {
            System.out.println("EXCEPTION >>>>>>>>>>>>>>>>> "+ e);
        }

        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomePage();
            }
        });
    }

    public void getDataFromSync() {
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                results = realm.where(TrackingGeoSpatial.class).findAll();
                List<String> vehicles=new ArrayList<String>();
                for (int i = 0; i < results.size(); i++) {
                    vehicles.add(results.get(i).toString());
                }
                renderListActivity(vehicles);
            }

        });

        results.addChangeListener(new RealmChangeListener<RealmResults<TrackingGeoSpatial>>() {
            @Override
            public void onChange(RealmResults<TrackingGeoSpatial> trackingGeoSpatials) {

                System.out.println("***************** HERE *****************"+ trackingGeoSpatials);
                List<String> vehicles=new ArrayList<String>();
                for (int i = 0; i < results.size(); i++) {
                    vehicles.add(results.get(i).toString());
                }
                renderListActivity(vehicles);

            }
        });
    }

    public void renderListActivity(List<String> vehicles) {
        System.out.println("!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>>>>"+vehicles);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vehicles);
        ListView listView = (ListView) findViewById(R.id.lvVehicle);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        backgroundThreadRealm.close();
        System.out.println(">>>>>>>> INside ADaptor view");
        String vehicle_data = adapterView.getItemAtPosition(pos).toString();
        System.out.println(vehicle_data);
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("key",vehicle_data);
        startActivity(i);
    }

    private void openHomePage() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, MainActivity.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, MainActivity.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }
}