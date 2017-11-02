package com.utn.mobile.keepapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
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

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificar("¡Recorda cargar tus ejercicios!");
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Transición desconocida: " + String.valueOf(geofenceTransition), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void notificar(String mensaje){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.keepapp_launcher)
                .setContentTitle("Saliste del gimnasio")
                .setContentText(mensaje);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Notification notification = mBuilder.build();

        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }
}