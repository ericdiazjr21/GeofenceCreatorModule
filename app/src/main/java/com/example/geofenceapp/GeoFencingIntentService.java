package com.example.geofenceapp;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;


public class GeoFencingIntentService extends IntentService {
    private static final String TAG = "GeoFencingIntentService";

    public GeoFencingIntentService() {
        super("GeoFencingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: intentFired!");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        int TransitionType = geofencingEvent.getGeofenceTransition();

        switch (TransitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                sendNotification("Just Entered Fence");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                sendNotification("Just Exited Fence");
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                sendNotification("Just Dwelling Fence");
                break;
            default:
                sendNotification("Invalid Transition Type");
                break;
        }

    }

    private void sendNotification(String message) {
        new Notification(this,message);
    }


}
