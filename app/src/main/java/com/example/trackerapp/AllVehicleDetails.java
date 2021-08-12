package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackerapp.Model.Tracking;
import com.example.trackerapp.Model.UserQuery;
import com.example.trackerapp.Model.Users;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.FindIterable;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class AllVehicleDetails extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String Appid = "application-0-wfzcl";
    private App app;
    public Realm realm;
    public List<String> vehicles=new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();


        /* Realm connection and auth */

        try{
            getDataFromSync();
        } catch (Exception e)
        {
            System.out.println("EXCEPTION >>>>>>>>>>>>>>>>> "+ e);
        }
    }

    public void getDataFromSync(){
        SyncSession.ClientResetHandler handler = new SyncSession.ClientResetHandler() {
            @Override
            public void onClientReset(SyncSession session, ClientResetRequiredError error) {
                Log.e("EXAMPLE", "Client Reset required for: " +
                        session.getConfiguration().getServerUrl() + " for error: " +
                        error.toString());
            }
        };


        App app = new App(new AppConfiguration.Builder(Appid)
                .defaultClientResetHandler(handler)
                .build());


        app.login(Credentials.anonymous());
//        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
//            @Override
//            public void onResult(App.Result<User> result) {
//                if(result.isSuccess())
//                {
//                    Log.v("User","Logged In Successfully");
//
//                }
//                else
//                {
//                    Log.v("User","Failed to Login");
//                }
//            }
//        });
        User user = app.currentUser();

        String partitionKey = "1";
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();

        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE", "Successfully opened a realm.");
                User user = app.currentUser();

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


                Document queryFilter  = new Document("partition_key","1");

                RealmResultTask<MongoCursor<UserQuery>> findTask = mongoCollection.find(queryFilter).iterator();

                findTask.getAsync(users -> {
                    if (users.isSuccess()) {
                        MongoCursor<UserQuery> results = users.get();
                        Log.v("EXAMPLE", "successfully found Data");
                        while (results.hasNext()) {
                            String result = results.next().toString();
                            vehicles.add(result);
                        }
                        renderListActivity(vehicles);
                    } else {
                        Log.e("EXAMPLE", "failed to find documents with: ", users.getError());
                    }
                });

                // Read all tasks in the realm. No special syntax required for synced realms.
//                List<Users> users = realm.where(Users.class).findAll();
//                output = findViewById(R.id.textView3);
//                output.setText("");
//                for(int i=0;i<users.size();i++){
//                    output.append("reg_no: "+users.get(i).get_id()+"- Name: "+users.get(i).getOwner_name());
//                    System.out.println(users.get(i).get_id());
//                }
//                System.out.println(users.get(0).get_id());
                realm.close();
            }
        });
    }

    public void renderListActivity(List<String> vehicles){
        System.out.println(vehicles);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vehicles);
        ListView listView = (ListView) findViewById(R.id.lvVehicle);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        System.out.println(">>>>>>>> INside ADaptor view");
        String vehicle_data = adapterView.getItemAtPosition(pos).toString();
        System.out.println(vehicle_data);
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("key",vehicle_data);
        startActivity(i);
    }
}