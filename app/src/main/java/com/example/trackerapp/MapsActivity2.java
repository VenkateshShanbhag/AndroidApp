package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trackerapp.Model.Tracking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackerapp.databinding.ActivityMaps2Binding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    String Appid = "application-0-wfzcl";
    public List<RealmResults<Tracking>> tracking_data = new ArrayList<RealmResults<Tracking>>();
    double lat;
    double lon;
    String reg_num;
    Realm backgroundThreadRealm;
    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SyncSession.ClientResetHandler handler = new SyncSession.ClientResetHandler() {
            @Override
            public void onClientReset(SyncSession session, ClientResetRequiredError error) {
                Log.e("EXAMPLE", "Client Reset required for: " +
                        session.getConfiguration().getServerUrl() + " for error: " +
                        error.toString());
            }
        };

        /* Initialize app configuration and login */
        App app = new App(new AppConfiguration.Builder(Appid)
                .defaultClientResetHandler(handler)
                .build());
        app.login(Credentials.anonymous());
        User user = app.currentUser();

        syncConfigurations(user);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        refresh = findViewById(R.id.refresh2);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPage();
            }
        });
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +refresh);

    }

    public void syncConfigurations(User user){

        String partitionKey = "1";
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();

        backgroundThreadRealm = Realm.getInstance(config);
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                //RealmResults<Tracking> results = realm.where(Tracking.class).sort("Timestamp", Sort.DESCENDING).findAll();
                RealmResults<Tracking> results = realm.where(Tracking.class).sort("Timestamp",Sort.DESCENDING).distinct("reg_num").findAll();
                tracking_data.add(results);
            }
        });
        System.out.println(" ))))))))))))))>>>>>>>>>>>>>>>>>>> "+tracking_data);
        System.out.println(tracking_data.get(0));
    }

    private void refreshPage() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for (int i = 0; i < tracking_data.get(0).size(); i++){
            lat = tracking_data.get(0).get(i).getLat();
            lon = tracking_data.get(0).get(i).getLon();
            reg_num = tracking_data.get(0).get(i).getReg_num();
            mMap = googleMap;
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney).title(reg_num +"\n "+ lat+" " +lon)).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
        backgroundThreadRealm.close();

    }
}