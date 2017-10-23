package com.utn.mobile.keepapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import com.google.android.gms.location.GeofencingEvent;


/**
 * Created by Fernando on 8/10/2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";
    private Handler handler;
    Context mContext;

    public GeofenceTransitionsIntentService() {
        super(TAG);  // use TAG to name the IntentService worker thread
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Toast.makeText(getApplicationContext(), "GeofencingEvent Error: " + event.getErrorCode(), Toast.LENGTH_SHORT).show();
            return;
        }

        final int geofenceTransition = event.getGeofenceTransition();

        if (geofenceTransition == 1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Adentro", Toast.LENGTH_SHORT).show();
                    //sendNotification();
                }
            });
        }

        else {
            // Log the error.
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Transición desconocida: " + String.valueOf(geofenceTransition), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendNotification() {
        //Notificación
    }
}