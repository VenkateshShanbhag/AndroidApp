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
    public App app1;
    String appid = "application-0-ykkzh";

    public String getAppid() {
        return appid;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}