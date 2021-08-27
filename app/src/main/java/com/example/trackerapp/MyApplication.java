package com.example.trackerapp;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

import io.realm.Realm;
import io.realm.mongodb.App;

public class MyApplication extends Application  {
    public App app1;
    String appid = "application-0-ykkzh";
    public int networkFlag=0;

    public String getAppid() {
        return appid;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

//    public int checkConnectivity(){
//        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
//        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
//            @Override
//            public void onAvailable(Network network) {
//                networkFlag = 1;
//                System.out.println("!!!!!!!!The default network is now: " + network);
//            }
//            @Override
//            public void onLost(Network network) {
//                System.out.println("!!!!!!!!The application no longer has a default network. The last default network was " + network);
//            }
//
//            @Override
//            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
//                System.out.println("!!!!!!!!The default network changed capabilities: " + networkCapabilities);
//            }
//
//            @Override
//            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
//                System.out.println("!!!!!!!!The default network changed link properties: " + linkProperties);
//            }
//        });
//        if(networkFlag==1){
//            return 1;
//        }
//        else{
//            return 0;
//        }
//    }
}