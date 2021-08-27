package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trackerapp.Model.Tracking;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackerapp.databinding.ActivityMaps2Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

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
    String Appid;
    public List<RealmResults<Tracking>> tracking_data = new ArrayList<RealmResults<Tracking>>();
    double lat;
    double lon;
    String reg_num;
    Realm backgroundThreadRealm;
    Button refresh;
    private GeofencingClient geofencingClient;
    private GeoFenceHelaper geoFenceHelaper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication dbConfigs = new MyApplication();
        Appid = dbConfigs.getAppid();
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

        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelaper = new GeoFenceHelaper(this);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + refresh);

    }

    public void syncConfigurations(User user) {
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
                RealmResults<Tracking> results =
                        realm.where(Tracking.class).sort("Timestamp", Sort.DESCENDING).distinct("reg_num").findAll();
                tracking_data.add(results);
            }
        });
        System.out.println(" ))))))))))))))>>>>>>>>>>>>>>>>>>> " + tracking_data);
        System.out.println(tracking_data.get(0));
    }

    private void refreshPage() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        for (int i = 0; i < tracking_data.get(0).size(); i++) {
            lat = tracking_data.get(0).get(i).getLat();
            lon = tracking_data.get(0).get(i).getLon();
            reg_num = tracking_data.get(0).get(i).getReg_num();
            // Add a marker in Sydney and move the camera
            LatLng custom = new LatLng(lat, lon);
            MarkerOptions marker = new MarkerOptions().position(custom).title(reg_num + "\n " + lat + " " + lon);
            marker.icon(bitmapDescriptorFromVector(this, R.mipmap.car_icon_03));
            mMap.addMarker(marker).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
        }
        addGeofence(new LatLng(14.24166, 74.448394), 10000f);
        addCircle(new LatLng(14.24166, 74.448394), 10000f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        backgroundThreadRealm.close();

    }

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geoFenceHelaper.getGeofence("Geofence-tracking", latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geoFenceHelaper.getGeofencingRequest(geofence);
//        PendingIntent pendingIntent = geoFenceHelaper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
//        .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Log.d("INFO", "GEOFENCE ADDED !!!!");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull @NotNull Exception e) {
//                Log.e("ERROR :","The geofence intent failed");
//            }
//        });
        System.out.println("GEOFENCE CLASS INITIATED !!!!!!!!");
    }

    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(128,0,255,255));
        circleOptions.fillColor(Color.argb(64,0,128,255));
        circleOptions.strokeWidth(3);
        mMap.addCircle(circleOptions);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}