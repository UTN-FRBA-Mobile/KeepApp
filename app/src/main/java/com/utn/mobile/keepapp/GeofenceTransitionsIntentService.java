package com.utn.mobile.keepapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando on 8/10/2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {


    protected static final String TAG = "GeofenceTransitionsIS";
    private Handler handler;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String EmailName, MessageEmail;
    Context mContext;
    Boolean EmailSent = false;

    public GeofenceTransitionsIntentService() {
        super(TAG);  // use TAG to name the IntentService worker thread
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            return;
        }

        /*
        String description = getGeofenceTransitionDetails(event);
        sendNotification(description);
        */

        final int geofenceTransition = event.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == 1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Entered", Toast.LENGTH_SHORT).show();
                    sendNotification();
                }
            });
            Log.i(TAG, "Entered");


        }

        else {
            // Log the error.
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Sorry you are out", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, String.valueOf(geofenceTransition));
        }
    }

    private static String getGeofenceTransitionDetails(GeofencingEvent event) {
        String transitionString =
                GeofenceStatusCodes.getStatusCodeString(event.getGeofenceTransition());
        List triggeringIDs = new ArrayList();
        for (Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs));
    }

    /*
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts MainActivity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainMenuActivity.class);

        // Get a PendingIntent containing the entire back stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainMenuActivity.class).addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Click notification to return to App")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        // Fire and notify the built Notification.
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
    */
    private void sendNotification() {
        Toast.makeText(GeofenceTransitionsIntentService.this, "HEEEEEEEY I'M IN", Toast.LENGTH_SHORT).show();
    }
}