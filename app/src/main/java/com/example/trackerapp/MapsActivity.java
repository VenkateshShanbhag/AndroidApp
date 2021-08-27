package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trackerapp.Model.Tracking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackerapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private GoogleMap googleMapHomeFrag;
    LatLng driverLatLng;
    private ActivityMapsBinding binding;
    String Appid;
    private App app;
    public Realm realm;
    public double lat;
    public double lon;
    public List<Tracking> tracking_data = new ArrayList<Tracking>();
    public String[] registration_number;
    String value;
    Button refresh;
    Button show_timeline;
    String partitionKey = "1";
    public List<String> vehicles = new ArrayList<String>();
    private JSONArray jsonObject;
    private GeofencingClient geofencingClient;
    List<String> latList = new ArrayList<String>();
    List<String> lonList = new ArrayList<String>();
    List<LatLng> latlonList = new ArrayList<LatLng>();
    int vehicleTimeline = 0;
    MyApplication dbConfigs = new MyApplication();
    int networkFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Appid = dbConfigs.getAppid();
        super.onCreate(savedInstanceState);
        // Get the value passed from intent in previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
            Log.v("Intent Value", value);
        }
        registration_number = value.split(" - ", 2);
        System.out.println("REGISTRATION NUMBER" + registration_number[1].toUpperCase());

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

        syncConfigurations(user);
        //GeoFence Activity
//        geofencingClient = LocationServices.getGeofencingClient(this);



        // MAP Activity
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPage();
            }
        });

        show_timeline = findViewById(R.id.timeline);
        try {
            show_timeline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showVehicleTimeline();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("NETWORK: ", "PLEASE CHEK YOUR NETWORK CONNECTION");
        }
    }


    public void showVehicleTimeline() throws IOException, JSONException {
        vehicleTimeline = 1;
        refreshPage();
    }


    public void syncConfigurations(User user) {
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();

        Realm backgroundThreadRealm = Realm.getInstance(config);
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Tracking results = realm.where(Tracking.class).sort("Timestamp", Sort.DESCENDING).equalTo("reg_num", registration_number[1].toUpperCase()).findFirst();
                System.out.println(results);
                tracking_data.add(results);
            }
        });

        for (int i = 0; i < tracking_data.size(); i++) {
            lat = tracking_data.get(i).getLat();
            lon = tracking_data.get(i).getLon();
        }

        backgroundThreadRealm.close();
    }

    private void refreshPage() {
        Intent intent = getIntent();
        intent.putExtra("key", value);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        geofenceList.add(new Geofence.Builder()
//                .setRequestId("GeoFence 01")
//                .setCircularRegion(35, 75, 500f)
//                .setExpirationDuration(60 * 60 * 1000)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                .build());
//        System.out.println(geofenceList);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        geofencingClient.addGeofences(getGeofencingRequest(), null)
//            .addOnSuccessListener(this, new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    System.out.println("GeoFence Added");
//                }
//            })
//            .addOnFailureListener(this, new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    System.out.println("FAILED to ADD GeoFence");
//                }
//            });
        try {
            System.out.println("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-ykkzh/service/tracking-data-api/incoming_webhook/webhook0?reg_num="+registration_number[1].toUpperCase());
            URL url = new URL("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-ykkzh/service/tracking-data-api/incoming_webhook/webhook0?reg_num="+registration_number[1].toUpperCase());
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
                }

                for(int i = 0; i< latList.size(); i++) {
                    double lat1 = Double.parseDouble(latList.get(i));
                    double lon1 = Double.parseDouble(lonList.get(i));
                    LatLng latlan = new LatLng(lat1,lon1);
                    latlonList.add(latlan);
                }
                System.out.println(latlonList);
            }
            PolylineOptions opts = new PolylineOptions();
            for (LatLng location : latlonList) {
                opts.add(location).clickable(true);
            }

            Polyline polyline1 = googleMap.addPolyline(opts);
            System.out.println("|||||||||-LATLON TIMELINE-|||||||||");
        } catch (Exception e){
            System.out.println("EXCEPTION: "+e);
        }



        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng custom = new LatLng(lat, lon);
        MarkerOptions marker = new MarkerOptions().position(custom).title(registration_number[1].toUpperCase());
        marker.icon(bitmapDescriptorFromVector(this, R.mipmap.car_icon_03));
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

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