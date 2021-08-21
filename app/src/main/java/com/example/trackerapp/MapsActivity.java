package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.trackerapp.Model.Tracking;
import com.example.trackerapp.Model.UserQuery;
import com.example.trackerapp.Model.Users;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackerapp.databinding.ActivityMapsBinding;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;
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
    String Appid = "application-0-wfzcl";
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
    public List<String> vehicles=new ArrayList<String>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the value passed from intent in previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
            Log.v("Intent Value" , value);
            //The key argument here must match that used in the other activity
        }

        registration_number = value.split(" - ", 2);

        System.out.println("REGISTRATION NUMBER"+registration_number[1].toUpperCase());

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
        // System.out.println();

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
        try{
            show_timeline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showVehicleTimeline();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            Log.e("NETWORK: ", "PLEASE CHEK YOUR NETWORK CONNECTION");
        }
    }

    public void showVehicleTimeline() throws IOException {

        URL url = new URL("https://us-east-1.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-wfzcl/service/tracking-data-api/incoming_webhook/tracking-data-api?reg_num=KA47MH1234");
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) url.openConnection();
        conection.setRequestMethod("GET");
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            // TODO: Parse the result and display in maps
            System.out.println("JSON String Result " + response.toString());
            //GetAndPost.POSTRequest(response.toString());
        } else {
            System.out.println("GET NOT WORKED");
        }
    }


    public void syncConfigurations(User user){
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();

        Realm backgroundThreadRealm = Realm.getInstance(config);
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Tracking results = realm.where(Tracking.class).sort("Timestamp", Sort.DESCENDING).equalTo("reg_num", registration_number[1].toUpperCase()).findFirst();
                tracking_data.add(results);
            }
        });

        for (int i = 0; i < tracking_data.size(); i++){
            lat = tracking_data.get(i).getLat();
            lon = tracking_data.get(i).getLon();
        }

        backgroundThreadRealm.close();
    }

    private void refreshPage() {
        Intent intent = getIntent();
        finish();
        intent.putExtra("key",value);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng custom = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(custom).title(registration_number[1].toUpperCase()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
    }
}