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
                    notificar("¡ADENTRO!");
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

    public void notificar(String mensaje){


        // Creo la notificacion
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.keepapp_launcher)
                .setContentTitle("Saliste del gimnasio")
                .setContentText(mensaje);


        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        // Para que se abra el login cuando clickeo la notificacion
        // TODO: por ahora este codigo abre una actividad nueva, yo quiero que habra la vieja
        /*Intent resultIntent = new Intent(this.context, Login.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);*/

        // Activo la notificacion
        Notification notification = mBuilder.build();
        //notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }
}