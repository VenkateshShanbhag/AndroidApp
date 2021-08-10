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

        Realm.init(this);
        app = new App(new AppConfiguration.Builder(Appid).build());

        app.login(Credentials.anonymous());

        // TODO: Implement Login using user creds.
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

        Users task = new Users();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setOwner_name(name.getText().toString());
                task.set_id(reg_num.getText().toString());
                task.setCity_of_purchase(city.getText().toString());
                task.setPartition_key("1");

                showCustomDialog();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                String partitionValue = "1";
                SyncConfiguration config = new SyncConfiguration.Builder(
                        user,
                        partitionValue).allowWritesOnUiThread(true).allowQueriesOnUiThread(true)
                        .build();
                Realm backgroundThreadRealm = Realm.getInstance(config);
                backgroundThreadRealm.executeTransaction (transactionRealm -> {
                    transactionRealm.insert(task);
                    System.out.println("Instered successfully !!!!!!!!!!!!!!!!!!!!");
                });

                backgroundThreadRealm.close();
            }
        });

//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                User user = app.currentUser();
//                mongoClient = user.getMongoClient("mongodb-atlas");
//                mongoDatabase = mongoClient.getDatabase("vehicle");
//                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("users");
//                mongoCollection.insertOne(new Document("name", name.getText().toString()).append("reg_no",reg_num.getText().toString())).getAsync( result -> {
//                    if(result.isSuccess()){
//                        System.out.println("Data insterted successfully");
//                        showCustomDialog();
//                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                        startActivity(i);
//                    }
//                    else {
//                        System.out.println("!!!!!!!!!!!!!!!! FAILURE >>>>>>>>>>>>>>>>>>>>");
//                    }
//                });
//
//            }
//        });


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

//public class AddVehicle extends AppCompatActivity {
//    EditText name;
//    EditText reg_num;
//    Button btnSave;
//    Realm realm;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_vehicle);
//        name = findViewById(R.id.name);
//        reg_num = findViewById(R.id.reg_no);
//        btnSave = findViewById(R.id.btn_save);
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String appId = "application-0-aayit";
//                App app = new App(new AppConfiguration.Builder(appId)
//                        .build());
//
//                Credentials credentials = Credentials.anonymous();
//
//                app.loginAsync(credentials, result -> {
//                    if (result.isSuccess()) {
//                        Log.v("QUICKSTART", "Successfully authenticated anonymously.");
//                        User user = app.currentUser();
//                        // interact with realm using your user object here
//                    } else {
//                        Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
//                    }
//                });
//
//                System.out.println("\n\n LOGGED IN\n\n");
//
//                SyncConfiguration configuration = new SyncConfiguration.Builder(
//                        app.currentUser(), "123"
//                ).allowQueriesOnUiThread(true).allowWritesOnUiThread(true).build();
//
//                realm = Realm.getDefaultInstance();
//                realm.executeTransactionAsync(new Realm.Transaction() {
//                                                  @Override
//                                                  public void execute(Realm bgRealm) {
//                                                      //create vehicle object
//
//                                                      Tracker vehicle = bgRealm.createObject(Tracker.class, UUID.randomUUID().toString());
//                                                      vehicle.setName(name.getText().toString());
//                                                      vehicle.setReg_no(reg_num.getText().toString());
//                                                      bgRealm.copyFromRealm(vehicle);
//
//                                                      List<Tracker> dataModals = new ArrayList<>();
//                                                      dataModals = bgRealm.where(Tracker.class).findAll();
//                                                      System.out.println(dataModals);
//
//
//
//                                                      System.out.println("SUCCESSFULLY ADDED THE DATA TO REALM DB");
//                                                  }
//                                              }, new Realm.Transaction.OnSuccess() {
//                                                  @Override
//                                                  public void onSuccess() {
//                                                      RealmResults<Tracker> tracker = realm.where(Tracker.class).findAll();
//                                                      System.out.println(tracker);
//
//                                                      System.out.println("Realm Success");
//                                                      realm.close();
//                                                  }
//
//
//                                              }, new Realm.Transaction.OnError() {
//                                                  @Override
//                                                  public void onError(Throwable error) {
//                                                      System.out.println("Realm Failed");
//                                                      System.out.println(error);
//                                                  }
//                                              }
//                );
//            }
//        });
//    }
//
//}