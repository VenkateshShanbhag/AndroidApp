//package com.example.trackerapp;
//
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofencingEvent;
//
//import java.util.List;
//
//public class GeofenceBroadcastReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO: This method is called when the BroadcastReceiver is receiving
//        // an Intent broadcast.
//        System.out.println("GEOFENCE TRIGGERED!!!!!!");
//        Toast.makeText(context, "geofence Triggered",Toast.LENGTH_SHORT).show();
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        if(geofencingEvent.hasError()){
//            Log.d("GEOFENCEERROR","onReceive :Error receiving Geofence event");
//            return;
//        }
//        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
//        for(Geofence geofence: geofenceList){
//            Log.d("GEOFENCEEVENT","onReceive:"+geofence.getRequestId());
//        }
//        int transactionType = geofencingEvent.getGeofenceTransition();
//        switch (transactionType){
//            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                Toast.makeText(context, "Entered Geofence", Toast.LENGTH_LONG).show();
//                break;
//            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                Toast.makeText(context, "Exited Geofence", Toast.LENGTH_LONG).show();
//                break;
//        }
//    }
//}