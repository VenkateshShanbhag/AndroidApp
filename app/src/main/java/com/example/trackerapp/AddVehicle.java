package com.example.trackerapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.trackerapp.Model.Users;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.sync.SyncConfiguration;

public class AddVehicle extends AppCompatActivity {
    String Appid = "application-0-wfzcl";
    private App app;
    EditText name;
    EditText reg_num;
    EditText city;
    Button btnSave;


    MongoDatabase mongoDatabase;
    MongoClient mongoClient;


    public static final String TAG = "ServerAuthCodeActivity";
    private static final int RC_GET_AUTH_CODE = 9003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle);

        name = findViewById(R.id.name);
        reg_num = findViewById(R.id.reg_no);
        city = findViewById(R.id.city);
        btnSave = findViewById(R.id.btn_save);

        // Initialized in MyApplication.java file
        //Realm.init(this);
        app = new App(new AppConfiguration.Builder(Appid).build());

        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    Log.v("User", "Logged In Successfully");
                } else {
                    Log.v("User", "Failed to Login");
                }
            }
        });

        User user = app.currentUser();

        Users user_data = new Users();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_data.setOwner_name(name.getText().toString());
                user_data.set_id(reg_num.getText().toString());
                user_data.setCity_of_purchase(city.getText().toString());
                user_data.setPartition_key("1");

                showCustomDialog();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                String partitionValue = "1";
//                RealmConfiguration config = new RealmConfiguration.Builder()
//                        .build();
//                Realm realm = Realm.getInstance(config);
//                realm.executeTransactionAsync(transactionRealm -> { // start a write transaction
//                    transactionRealm.insert(task);
//                    System.out.println("Instered successfully !!!!!!!!!!!!!!!!!!!!");
//                });
                // Sync the realm db data with remote network
                SyncConfiguration config = new SyncConfiguration.Builder(user, partitionValue)
                        .allowWritesOnUiThread(true)
                        .allowQueriesOnUiThread(true)
                        .build();
                Realm backgroundThreadRealm = Realm.getInstance(config);
                backgroundThreadRealm.executeTransaction(transactionRealm -> {
                    transactionRealm.insert(user_data);
                    System.out.println("Instered successfully !!!!!!!!!!!!!!!!!!!!");
                });
                backgroundThreadRealm.close();
            }
        });
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.closeOptionsMenu();

    }
}