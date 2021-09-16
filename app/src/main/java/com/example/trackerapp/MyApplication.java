package com.example.trackerapp;

import android.app.Application;
import android.util.Log;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.mongodb.App;

public class MyApplication extends Application  {
    public App app1;
    String appid = "application-0-ykkzh";
    Double static_lat = 12.9716;
    Double static_lon = 77.5946;

    public String getAppid() {
        return appid;
    }

    public Double getStatic_lat() { return static_lat; }
    public Double getStatic_lon() { return static_lon; }


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}