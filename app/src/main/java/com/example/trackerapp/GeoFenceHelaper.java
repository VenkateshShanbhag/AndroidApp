package com.example.trackerapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeoFenceHelaper extends ContextWrapper {

    private static final String TAG = "GeofenceHelaper";
    PendingIntent pendingIntent;

    public GeoFenceHelaper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geoFence){
        return new GeofencingRequest.Builder()
                .addGeofence(geoFence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public Geofence getGeofence(String Id, LatLng latLng, float radius, int transactionType){
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude,latLng.longitude,radius)
                .setRequestId(Id)
                .setTransitionTypes(transactionType)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

//    public PendingIntent getPendingIntent(){
//        if(pendingIntent!=null){
//            return pendingIntent;
//        }
//        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        return pendingIntent;
//    }

    public String getErrorString(Exception e){
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            System.out.println("geofence exception");
        }
        return e.toString();
    }

}
