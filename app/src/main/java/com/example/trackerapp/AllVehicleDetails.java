package com.example.trackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackerapp.Model.Tracking;
import com.example.trackerapp.Model.Users;

import org.bson.Document;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.FindIterable;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class AllVehicleDetails extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String Appid = "application-0-wfzcl";
    private App app;
    public Realm realm;
    MongoClient client;
    ListView listView;
    TextView output;
    String[] months;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        /* Realm connection and auth */
        Realm.init(this);

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

        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if(result.isSuccess())
                {
                    Log.v("User","Logged In Successfully");

                }
                else
                {
                    Log.v("User","Failed to Login");
                }
            }
        });
        User user = app.currentUser();

        System.out.println(user);
        String partitionKey = "1";
        SyncConfiguration config = new SyncConfiguration.Builder(
                user,
                partitionKey).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                .build();



        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE", "Successfully opened a realm.");
                // Read all tasks in the realm. No special syntax required for synced realms.
                List<Users> users = realm.where(Users.class).findAll();



//                output = findViewById(R.id.textView3);
//                output.setText("");
//                for(int i=0;i<users.size();i++){
//                    output.append("ID : "+users.get(i).get_id()+" Name : "+users.get(i).getOwner_name()+" Age : "+users.get(i).getCity_of_purchase()+" \n");
//                }
                // Write to the realm. No special syntax required for synced realms.
                // Don't forget to close your realm!
                realm.close();
            }
        });



//
//        realm = Realm.getInstance(config);
//
//
//        RealmResults<Users> vehicle_details = realm.where(Users.class).findAll();
//        System.out.println(vehicle_details);


//        output.setText("");
//        for(int i=0;i<users_data.size();i++){
//            output.append("ID : "+users_data.get(i).get_id()+" Name : "+users_data.get(i).getOwner_name()+" \n");
//        }

//        months = new DateFormatSymbols().getMonths();
//        //ArrayAdapter<String> arr    java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.trackerapp/com.example.trackerapp.AllVehicleDetails}: java.lang.ClassCastException: io.realm.internal.async.RealmResultTaskImpl cannot be cast to org.bson.DocumentayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, months);
//        ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(this, R.layout.list_item_vehicle, Collections.singletonList(users_data));
//        listView.setAdapter(arrayAdapter);
//        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Object month = adapterView.getItemAtPosition(pos).toString();
        System.out.println(month);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        //Toast.makeText(getApplicationContext(), "Clicked: "+ month, Toast.LENGTH_SHORT).show();

    }
}