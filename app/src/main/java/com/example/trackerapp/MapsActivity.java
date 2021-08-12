package com.example.trackerapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    String Appid = "application-0-wfzcl";
    private App app;
    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the value passed from intent in previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            Log.v("Intent Value" , value);
            //The key argument here must match that used in the other activity
        }

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

        // MAP Activity
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MongoCollection<UserQuery> mongoCollection = getCollection(user);
        Log.v("Mongo Collection Object >>>>>>", String.valueOf(mongoCollection));


        //TODO: Implement check Network availability before polling the data for maps
//        while (true){
//            getTrackingData(mongoCollection);
//        }
    }

    public MongoCollection getCollection(User user){
        MongoClient mongoClient =
                user.getMongoClient("mongodb-atlas");

        MongoDatabase mongoDatabase =
                mongoClient.getDatabase("vehicle");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoCollection<UserQuery> mongoCollection =
                mongoDatabase.getCollection(
                        "users",
                        UserQuery.class).withCodecRegistry(pojoCodecRegistry);

        Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle");
        return mongoCollection;
    }

    public void getTrackingData(MongoCollection mongoCollection){
        Tracking tracking_data = new Tracking();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /* Realm connection and auth */


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}