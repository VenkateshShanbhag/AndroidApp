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

import com.example.trackerapp.Model.Tracking;
import com.example.trackerapp.Model.Users;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class AddVehicle extends AppCompatActivity {
    String Appid;
    private App app;
    EditText name;
    EditText reg_num;
    EditText city;
    Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication dbConfigs = new MyApplication();
        Appid = dbConfigs.getAppid();
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
        Tracking tracking_data = new Tracking();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String owner_name = name.getText().toString();
                String registration_num = reg_num.getText().toString();
                String city_of_reg = city.getText().toString();
                user_data.setOwner_name(owner_name.toUpperCase());
                user_data.set_id(registration_num.toUpperCase());
                user_data.setCity_of_purchase(city_of_reg.toUpperCase());
                user_data.setPartition_key("1");
                tracking_data.setTimestamp(new Date());
                tracking_data.setReg_num(registration_num.toUpperCase());
                tracking_data.setPartition_key("1");
                tracking_data.setLat((double) 0);
                tracking_data.setLon((double) 0);
                tracking_data.set_id(new ObjectId());

                showCustomDialog();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                String partitionValue = "1";
                SyncConfiguration config = new SyncConfiguration.Builder(user, partitionValue)
                        .allowWritesOnUiThread(true)
                        .allowQueriesOnUiThread(true)
                        .build();
                Realm backgroundThreadRealm = Realm.getInstance(config);
                backgroundThreadRealm.executeTransaction(transactionRealm -> {
                    transactionRealm.insert(user_data);
                    transactionRealm.insert(tracking_data);
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