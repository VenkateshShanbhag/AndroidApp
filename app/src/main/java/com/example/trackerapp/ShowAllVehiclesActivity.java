package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trackerapp.Model.Tracking;
import com.example.trackerapp.databinding.ActivityShowAllVehiclesBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
//
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class ShowAllVehiclesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityShowAllVehiclesBinding binding;
    String Appid;
    public List<RealmResults<Tracking>> tracking_data = new ArrayList<RealmResults<Tracking>>();
    double lat;
    double lon;
    String reg_num;
    Realm backgroundThreadRealm;
    Button refresh;
    Button add_vehicle;
    Button vehicle_list;
    List<String> latList = new ArrayList<String>();
    List<String> lonList = new ArrayList<String>();
    List<String> regNumList = new ArrayList<String>();
    List<LatLng> latlonList = new ArrayList<LatLng>();
    String partitionKey;
    List<String> timestampList = new ArrayList<String>();
    Tracking tracking=null;
    boolean inCircle;
    Location currentLocation;
    private boolean locationPermissionGranted;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;



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

        getLatestLatLon(user);
        readRealmData(user);

        binding = ActivityShowAllVehiclesBinding.inflate(getLayoutInflater());
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

        add_vehicle = findViewById(R.id.add_vehicle);
        add_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddVehiclePage();
            }
        });

        vehicle_list = findViewById(R.id.vehicle_list);
        vehicle_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVehicleList();
            }
        });
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
//        addGeofence(new LatLng(14.24166, 74.448394), 10000f);
        addCircle(new LatLng(14.24166, 74.448394), 10000f);
        filterMarkers(10000f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        backgroundThreadRealm.close();
    }

    /* Realm Sync init */
    public void syncConfigurations(User user) {
        partitionKey = "1";
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();

        backgroundThreadRealm = Realm.getInstance(config);

    }

    /* Sync data from Atlas to Realm db */
    //TODO: The query should read all the files. as the timeseries data is synced directly into Tracking data in updateLatestLatLon method
    public void readRealmData(User user){
        syncConfigurations(user);
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                // ... do something with the updates (UI, etc.) ...
                RealmChangeListener<Realm> realmListener = new RealmChangeListener<Realm>() {
                    @Override
                    public void onChange(Realm realm) {
                        RealmResults<Tracking> results =
                                realm.where(Tracking.class).sort("Timestamp", Sort.DESCENDING).distinct("reg_num").findAll();

                        tracking_data.add(results);
                    }
                };
                realm.addChangeListener(realmListener);
            }
        });
    }

    /* Refresh the page to retrive latest data */
    private void refreshPage() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /* Create fence circle on googlemaps */
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(128,0,255,255));
        circleOptions.fillColor(Color.argb(64,0,128,255));
        circleOptions.strokeWidth(3);
        mMap.addCircle(circleOptions);
    }

    private void filterMarkers(double radiusForCircle){
        float[] distance = new float[2];

        for (int i = 0; i < tracking_data.get(0).size(); i++) {
            double lat = tracking_data.get(0).get(i).getLat();
            double lon = tracking_data.get(0).get(i).getLon();
            String reg_num_1 = tracking_data.get(0).get(i).getReg_num();
            Location.distanceBetween(lat,lon, 14.24166,74.448394
                    , distance);

            inCircle = distance[0] <= radiusForCircle;
            System.out.println("IN CIRCLE: "+inCircle+ "   reg_num :"+reg_num_1);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void openAddVehiclePage() {
        Intent intent = new Intent(this, AddVehicle.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }

    private void showVehicleList(){
        Intent intent = new Intent(this, AllVehicleDetails.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }

    private void getLatestLatLon(User user){
        try {
            System.out.println("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-ykkzh/service/get-all-latlon/incoming_webhook/webhook1");
            URL url = new URL("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-ykkzh/service/get-all-latlon/incoming_webhook/webhook1");
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
                System.out.println("JSON String Result " + response_json_array.get(0));

                for(int i = 0; i<response_json_array.length(); i++){
                    String lat = response_json_array.getJSONObject(i).optString("lat");
                    JSONObject lat_json = new JSONObject(lat);
                    latList.add((String) lat_json.get("$numberDouble"));

                    String lon = response_json_array.getJSONObject(i).optString("lon");
                    JSONObject lon_json = new JSONObject(lon);
                    lonList.add((String) lon_json.get("$numberDouble"));

                    String reg_num = response_json_array.getJSONObject(i).optString("reg_num");
                    regNumList.add( reg_num);

                    String timestamp = response_json_array.getJSONObject(i).optString("Timestamp");
                    JSONObject timestamp_json = new JSONObject(timestamp);
                    JSONObject timestamp_long = new JSONObject(timestamp_json.get("$date").toString());

                    timestampList.add((String)timestamp_long.get("$numberLong"));
                }
                updateLatestLatLon(user);
                System.out.println(">>>>>>>"+latList);
                System.out.println(lonList);
                System.out.println(regNumList);
                System.out.println(timestampList);

            }
        } catch (Exception e){
            System.out.println("EXCEPTION: "+e);
        }
    }

    private void updateLatestLatLon(User user){
        syncConfigurations(user);
        backgroundThreadRealm.executeTransaction(transactionRealm -> {
            System.out.println(latList);
            for(int i=0; i< latList.size(); i++){
                double lat1 = Double.parseDouble(latList.get(i));
                double lon1 = Double.parseDouble(lonList.get(i));
                long timestamp = Long.parseLong(timestampList.get(i));
                Date date = new Date(timestamp);
                String reg_num = regNumList.get(i);
                tracking = transactionRealm.where(Tracking.class).equalTo("reg_num",reg_num).findFirst();
                if(tracking == null) {
                    tracking = new Tracking();  // or realm.createObject(Person.class, id);
                    tracking.set_id(new ObjectId());
                }
                tracking.setTimestamp(date);
                tracking.setPartition_key("1");
                tracking.setReg_num(reg_num);
                tracking.setLat(lat1);
                tracking.setLon(lon1);

                transactionRealm.insertOrUpdate(tracking);
            }

            System.out.println("Instered successfully !!!!!!!!!!!!!!!!!!!!");
        });
        backgroundThreadRealm.close();
    }
}