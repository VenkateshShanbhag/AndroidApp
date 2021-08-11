package com.example.trackerapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackerapp.databinding.ActivityMapsBinding;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    String Appid = "application-0-wfzcl";
    private App app;
    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            System.out.println(">>>>>>>>>>>>>>>>>>>"+value);
            //The key argument here must match that used in the other activity
        }
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /* Realm connection and auth */
        Realm.init(this);

        /* Sync Session */
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

        String partitionKey = "1";
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}