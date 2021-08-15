package com.example.trackerapp;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class MyApplication extends Application {
    public String Appid = "application-0-wfzcl";
    public App app1;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
//        SyncSession.ClientResetHandler handler = new SyncSession.ClientResetHandler() {
//            @Override
//            public void onClientReset(SyncSession session, ClientResetRequiredError error) {
//                Log.e("EXAMPLE", "Client Reset required for: " +
//                        session.getConfiguration().getServerUrl() + " for error: " +
//                        error.toString());
//            }
//        };
//        app1 = new App(new AppConfiguration.Builder(Appid).defaultClientResetHandler(handler)
//                .build());
    }
}